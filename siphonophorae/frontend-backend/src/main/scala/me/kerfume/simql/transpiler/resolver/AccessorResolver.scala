package me.kerfume.simql.transpiler.resolver

import me.kerfume.simql.transpiler._
import me.kerfume.simql.node._

import scala.util.{Failure, Success, Try}

object AccessorResolver extends Resolver {
  def resolve(ast: SimqlRoot, meta: ASTMetaData): Either[String, SimqlRoot] = {
    AccessorResolverVisitor.visit(ast).run(meta)
  }
}
object AccessorResolverVisitor extends ASTVisitor {
  import ASTVisitor._

  override def visit(node: SymbolWithAccessor): RE[SymbolWithAccessor] = re { meta =>
    node.accessor match {
      case None =>
        Right(
          node.copy(
            accessor = Some(
              Accessor(0, Some(meta.tables.head))
            )
          )
        )
      case Some(ac) =>
        Try { meta.tables(ac.point) } match {
          case Success(table) =>
            Right(
              node.copy(
                accessor = Some(
                  ac.copy(
                    resolvedSymbol = Some(table)
                  )
                )
              )
            )
          case Failure(_) =>
            Left(s"can't access table. index: $$${ac.point}")
        }
    }
  }
}
