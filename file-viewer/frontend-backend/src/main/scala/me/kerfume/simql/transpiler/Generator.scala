package me.kerfume.simql.transpiler

import me.kerfume.simql.node._

trait Generator {
  type Code = String
  def generate(ast: SimqlRoot): Code
}

object SQLGenerator extends Generator {
  def generate(ast: SimqlRoot): Code = syntax.toSQL(ast)

  object syntax {
    import scala.annotation.tailrec
    // TODO stack safe or @tailrec
    def toSQL(node: Node): String = node match {
      case t: Term => t match {
        case StringWrapper(v) => v.replaceAll("\"", "'")
        case NumberWrapper(v) => v.toString
        case SymbolWrapper(v) => v
        case SymbolWithAccessor(s, ac) =>
          val accessorToken = ac.map(a => s"${toSQL(a)}.").getOrElse("")
          s"${accessorToken}${toSQL(s)}"
      }
      // この時点で未解決のシンボルはない前提...
      case Accessor(_, resolveSymbol) => resolveSymbol.map(toSQL).getOrElse("")
      case BinaryOp(op) => op.label
      case LogicalOp(op) => op.label
      case JoinType(value) => value match {
        case JoinType.LeftJoin => "LEFT JOIN"
        case JoinType.InnerJoin => "INNER JOIN"
      }

      case BinaryCond(op, lhs, rhs) =>
        s"${toSQL(lhs)} ${toSQL(op)} ${toSQL(rhs)}"
      case ExprRhs(op, value) =>
        s"${toSQL(op)} ${toSQL(value)}"
      case Expr(lhs, rhss) =>
        s"${toSQL(lhs)} ${rhss.map(toSQL).mkString(" ")}"
      case Join(jt, rhsTable, on) =>
        s"${toSQL(jt)} ${toSQL(rhsTable)} ON ${toSQL(on)}"
      case From(lhs, rhss) =>
        s"${toSQL(lhs)} ${rhss.map(toSQL).mkString(" ")}"
      case Select(values) =>
        s"""${if(values.isEmpty) "*" else values.map(toSQL).mkString(" ")}"""
      case Where(value) =>
        s"${toSQL(value)}"
      case SimqlRoot(from, select, where) =>
        s"SELECT ${select.map(toSQL).getOrElse("*")} FROM ${toSQL(from)} ${where.map(w => s"WHERE ${toSQL(w)}").getOrElse("")}"
    }
  }
}
