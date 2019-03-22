package me.kerfume.simql.transpiler

import me.kerfume.simql.node._

trait Generator {
  type Code
  def generate(ast: SimqlRoot): Code
}

object MySQLGenerator extends Generator {
  type Code = String

  def generate(ast: SimqlRoot): Code = syntax.toSQL(ast)

  object syntax {
    import scala.annotation.tailrec
    // TODO stack safe or @tailrec
    def toSQL(node: Node): String = node match {
      case t: Term =>
        t match {
          case StringWrapper(v) => v.replaceAll("\"", "'")
          case NumberWrapper(v) => v.toString
          case NullLit          => "null"
          case SymbolWrapper(v) => v
          case SymbolWithAccessor(s, ac) =>
            val accessorToken = ac.map(a => s"${toSQL(a)}.").getOrElse("")
            s"${accessorToken}${toSQL(s)}"
        }
      // この時点で未解決のシンボルはない前提...
      case Accessor(_, resolveSymbol) => resolveSymbol.map(toSQL).getOrElse("")
      case BinaryOp(op)               => op.label
      case LogicalOp(op)              => op.label
      case JoinType(value) =>
        value match {
          case JoinType.LeftJoin  => "LEFT JOIN"
          case JoinType.InnerJoin => "INNER JOIN"
        }
      case OrderType(value) =>
        value match {
          case OrderType.ASC  => "ASC"
          case OrderType.DESC => "DESC"
        }

      case c: Cond =>
        c match {
          case BinaryCond(op, lhs, rhs) =>
            s"${toSQL(lhs)} ${toSQL(op)} ${toSQL(rhs)}"
          case IsNull(lhs) =>
            s"${toSQL(lhs)} IS NULL"
          case IsNotNull(lhs) =>
            s"${toSQL(lhs)} IS NOT NULL"
        }
      case ExprRhs(op, value) =>
        s"${toSQL(op)} ${toSQL(value)}"
      case Expr(lhs, rhss) =>
        s"${toSQL(lhs)} ${rhss.map(toSQL).mkString(" ")}"
      case Join(jt, rhsTable, on) =>
        s"${toSQL(jt)} ${toSQL(rhsTable)} ON ${toSQL(on)}"
      case From(lhs, rhss) =>
        s"${toSQL(lhs)} ${rhss.map(toSQL).mkString(" ")}"
      case Select(values) =>
        s"""${if (values.isEmpty) "*" else values.map(toSQL).mkString(", ")}"""
      case Where(value) =>
        s"${toSQL(value)}"
      case LimitOffset(limit, offset) =>
        val offsetSQL = offset.map(o => s"${toSQL(o)}, ").getOrElse("")
        s"$offsetSQL ${toSQL(limit)}" // mysql dialect
      case Order(tpe, head, tail) =>
        def pairToSQL(tpe: OrderType, symbol: SymbolWithAccessor): String = {
          s"${toSQL(symbol)} ${toSQL(tpe)}"
        }
        val tailSyntax = tail.map { t =>
          s", ${pairToSQL(tpe, t)}"
        }.mkString(" ")
        s"${pairToSQL(tpe, head)} $tailSyntax"

      case SimqlRoot(from, select, where, limit, order) =>
        val selectSQL = select.map(toSQL).getOrElse("*")
        val whereSQL = where.map(w => s"WHERE ${toSQL(w)}").getOrElse("")
        val limitOffsetSQL = limit.map(l => s"LIMIT ${toSQL(l)}").getOrElse("")
        val orderSQL = order.map(o => s"ORDER BY ${toSQL(o)}").getOrElse("")
        s"SELECT $selectSQL FROM ${toSQL(from)} $whereSQL $limitOffsetSQL $orderSQL"
    }
  }
}
