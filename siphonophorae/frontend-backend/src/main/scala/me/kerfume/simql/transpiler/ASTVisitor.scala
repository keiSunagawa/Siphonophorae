package me.kerfume.simql.transpiler

import me.kerfume.simql.node._
import me.kerfume.simql.functions._
import cats.instances.list._
import cats.instances.either._

trait ASTVisitor {
  import ASTVisitor._

  def visit(node: StringWrapper): RE[StringWrapper] = re { _ =>
    Right(node)
  }
  def visit(node: NumberWrapper): RE[NumberWrapper] = re { _ =>
    Right(node)
  }
  def visit(node: Raw): RE[Raw] = re { _ =>
    Right(node)
  }
  def visit(node: SymbolWrapper): RE[SymbolWrapper] = re { _ =>
    Right(node)
  }
  def visit(node: SymbolWithAccessor): RE[SymbolWithAccessor] = re { _ =>
    Right(node)
  }
  def visitMacroApply(node: MacroApply): RE[MacroApply] = re { _ =>
    Right(node)
  }

  def visitHighSymbol(node: HighSymbol): RE[HighSymbol] = node match {
    case n: Raw                => (visit(n): RE[Raw]).map(identity)
    case n: SymbolWithAccessor => (visit(n): RE[SymbolWithAccessor]).map(identity)
    case n: MacroApply         => visitMacroApply(n).map(identity)
  }

  def visit(node: Term): RE[Term] = node match {
    case n: StringWrapper => (visit(n): RE[StringWrapper]).map(identity)
    case n: NumberWrapper => (visit(n): RE[NumberWrapper]).map(identity)
    case n: SymbolWrapper => (visit(n): RE[SymbolWrapper]).map(identity)
    case n: HighSymbol    => visitHighSymbol(n).map(identity)
    case NullLit =>
      re { _ =>
        Right(NullLit)
      }
  }

  def visit(node: BinaryCond): RE[BinaryCond] = {
    for {
      lhs <- visitHighSymbol(node.lhs)
      rhs <- visit(node.rhs)
    } yield
      node.copy(
        lhs = lhs,
        rhs = rhs
      )
  }
  def visit(node: IsNull): RE[IsNull] = {
    for {
      lhs <- visitHighSymbol(node.lhs)
    } yield
      node.copy(
        lhs = lhs
      )
  }
  def visit(node: IsNotNull): RE[IsNotNull] = {
    for {
      lhs <- visitHighSymbol(node.lhs)
    } yield
      node.copy(
        lhs = lhs
      )
  }

  def visit(node: Cond): RE[Cond] = node match {
    case n: BinaryCond => (visit(n): RE[BinaryCond]).map(identity)
    case n: IsNull     => (visit(n): RE[IsNull]).map(identity)
    case n: IsNotNull  => (visit(n): RE[IsNotNull]).map(identity)
  }

  def visit(node: ExprRhs): RE[ExprRhs] = {
    for {
      value <- visit(node.value)
    } yield
      node.copy(
        value = value
      )
  }

  def visit(node: Expr): RE[Expr] = {
    for {
      lhs <- visit(node.lhs)
      rhss <- re { env =>
               node.rhss.mapE(n => visit(n).run(env))
             }
    } yield
      node.copy(
        lhs = lhs,
        rhss = rhss
      )
  }

  def visit(node: Join): RE[Join] = {
    for {
      rhsTable <- visit(node.rhsTable)
      on <- visit(node.on)
    } yield
      node.copy(
        rhsTable = rhsTable,
        on = on
      )
  }

  def visit(node: From): RE[From] = {
    for {
      lhs <- visit(node.lhs)
      rhss <- re { env =>
               node.rhss.mapE(n => visit(n).run(env))
             }
    } yield
      node.copy(
        lhs = node.lhs,
        rhss = rhss
      )
  }

  def visit(node: Select): RE[Select] = {
    for {
      values <- re { env =>
                 node.values.mapE(n => visitHighSymbol(n).run(env))
               }
    } yield
      node.copy(
        values = values
      )
  }

  def visit(node: Where): RE[Where] = {
    for {
      value <- visit(node.value)
    } yield
      node.copy(
        value = value
      )
  }

  def visit(node: LimitOffset): RE[LimitOffset] = {
    for {
      limit <- visit(node.limit)
      offset <- re { env =>
                 transpose { node.offset.map(n => visit(n).run(env)) }
               }
    } yield
      node.copy(
        limit = limit,
        offset = offset
      )
  }

  def visit(node: Order): RE[Order] = {
    for {
      head <- visitHighSymbol(node.head)
      tail <- re { env =>
               node.tail.mapE(n => visitHighSymbol(n).run(env))
             }
    } yield
      node.copy(
        head = head,
        tail = tail
      )
  }

  def visit(node: SimqlRoot): RE[SimqlRoot] = {
    for {
      from <- visit(node.from)
      select <- re { env =>
                 transpose { node.select.map(n => visit(n).run(env)) }
               }
      where <- re { env =>
                transpose { node.where.map(n => visit(n).run(env)) }
              }
      limitOffset <- re { env =>
                      transpose { node.limitOffset.map(n => visit(n).run(env)) }
                    }
      order <- re { env =>
                transpose { node.order.map(n => visit(n).run(env)) }
              }
    } yield
      node.copy(
        from = from,
        select = select,
        where = where,
        limitOffset = limitOffset,
        order = order
      )
  }
}

object ASTVisitor {
  import cats.data.Kleisli
  // Reader Either
  type RE[A] = Kleisli[({ type F[B] = Either[TranspileError, B] })#F, ASTMetaData, A]
  def re[A](f: ASTMetaData => Either[TranspileError, A]): RE[A] =
    Kleisli[({ type F[B] = Either[TranspileError, B] })#F, ASTMetaData, A](f)
}
