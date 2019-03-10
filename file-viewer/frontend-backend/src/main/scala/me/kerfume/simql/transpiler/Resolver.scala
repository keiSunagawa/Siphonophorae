package me.kerfume.simql.transpiler

import me.kerfume.simql.node._

import scala.util.{ Try, Success, Failure }
trait Resolver {
  def resolve(ast: SimqlRoot, meta: ASTMetaData): Either[String, SimqlRoot]
}

// TODO visitor patternのほうがいいかも?
object AccessorResolver {
  def resolve(ast: SimqlRoot, meta: ASTMetaData): Either[String, SimqlRoot] = {
    val resolvedSelect =
      ast.select.map(_.values.foldLeft[Either[String, List[SymbolWithAccessor]]](Right(Nil)) { case (acm, unresolve) =>
        for {
          _acm <- acm
          resolved <- resolve0(unresolve, meta)
        } yield resolved :: _acm
      }) match  {
        // TODO ここがとくにひどい…
        case None => Right(ast.select)
        case Some(Right(cols)) => Right(
          ast.select.map(_.copy(
            values = cols
          ))
        )
        case Some(Left(e)) => Left(e)
      }

    // on句の解決
    val resolvedFrom = {
      val resolvedJoin = ast.from.rhss.foldLeft[Either[String, List[Join]]](Right(Nil)) { case (acm, unresolve) =>
        for {
          _acm <- acm
          resolvedExp <- exprResolve(unresolve.on, meta)
        } yield unresolve.copy(
          on = resolvedExp
        ) :: _acm
      }
      resolvedJoin.right.map { join =>
        ast.from.copy(
          rhss = join
        )
      }
    }

    val resolvedWhere =
      ast.where.map(w => exprResolve(w.value, meta)) match  {
        case None => Right(ast.where)
        case Some(Right(exprs)) => Right(
          ast.where.map(_.copy(
            value = exprs
          ))
        )
        case Some(Left(e)) => Left(e)
      }

    // 各句の置き換え
    for {
      s <- resolvedSelect
      f <- resolvedFrom
      w <- resolvedWhere
    } yield ast.copy(
      select = s,
      from = f,
      where = w
    )
  }

  private[this] def exprResolve(node: Expr, meta: ASTMetaData): Either[String, Expr] = {
    val resolvedLhs = bcondResolve(node.lhs, meta)
    val resolvedRhss = node.rhss.foldLeft[Either[String, List[ExprRhs]]](Right(Nil)) { case (acm, unresolved) =>
      for {
        _acm <- acm
        resolvedValue <- bcondResolve(unresolved.value, meta)
      } yield unresolved.copy(
        value = resolvedValue
      ) :: _acm
    }
    for {
      lhs <- resolvedLhs
      rhss <- resolvedRhss
    } yield node.copy(
      lhs = lhs,
      rhss = rhss
    )
  }

  private[this] def bcondResolve(node: BinaryCond, meta: ASTMetaData): Either[String, BinaryCond] = {
    val resolvedLhs = termResolve(node.lhs, meta)
    val resolvedRhs = termResolve(node.rhs, meta)
    for {
      lhs <- resolvedLhs
      rhs <- resolvedRhs
    } yield node.copy(
      lhs = lhs,
      rhs = rhs
    )
  }

  private[this] def termResolve(node: Term, meta: ASTMetaData): Either[String, Term] = {
    node match {
      case sa: SymbolWithAccessor => resolve0(sa, meta)
      case _ => Right(node)
    }
  }

  // TODO accessorなしのときは$0を適用
  private[this] def resolve0(node: SymbolWithAccessor, meta: ASTMetaData): Either[String, SymbolWithAccessor] = {
    node.accessor match {
      case None => Right(node.copy(
        accessor = Some(
          Accessor(0, Some(meta.tables.head))
        )
      ))
      case Some(ac) =>
        Try { meta.tables(ac.point) } match {
          case Success(table) =>
            Right(node.copy(
              accessor = Some(ac.copy(
                resolvedSymbol = Some(table)
              ))
            ))
          case Failure(_) =>
            Left(s"can't access table. index: $$${ac.point}")
        }
    }
  }
}
