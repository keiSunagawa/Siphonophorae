package me.kerfume

import scala.util.parsing.combinator._
import Command._

object Parser extends JavaTokenParsers {
  def decodeOrder(value: String): Option[Order] = {
    parse(order, value) match {
      case Success(o, a) if a.atEnd =>
        Some(o)
      case _ => None
    }
  }
  def decodeFilter(value: String): Option[Filter] = {
    parse(filter, value) match {
      case Success(f, a) if a.atEnd =>
        Some(f)
      case _ => None
    }
  }
  def decodeExpr(value: String): Option[Expr] = {
    parse(expr, value) match {
      case Success(f, a) if a.atEnd =>
        Some(f)
      case _ => None
    }
  }

  private[this] val column: Parser[String] = """[a-zA-Z0-9]+""".r ^^ { _.toString }

  private[this] val orderType: Parser[OrderType] = """(asc|desc)""".r ^^ {
    case "asc" => Asc
    case "desc" => Desc
  }

  private[this] val numFilterType: Parser[FilterOperator[NumberC]] = """(>=|<=|>|<)""".r~wholeNumber ^^ { case op~numstr =>
    val n = numstr.toInt
    op match {
      case ">=" => GE(n)
      case ">" => GT(n)
      case "<=" => LE(n)
      case "<" => LT(n)
    }
  }

  private[this] val strFilterType: Parser[FilterOperator[StringC]] = """(in|=)""".r~(wholeNumber|stringLiteral) ^^ { case op~s =>
    val reps = s.replaceAll("\"", "")
    op match {
      case "in" => IN(reps)
      case "=" => EQ(reps)
    }
  }

  val exprOperation: Parser[ExprOperator] = """(\+|-|\*|/)""".r ^^ { case op =>
    op match {
      case "+" => `+`
      case "-" => `-`
      case "*" => `*`
      case "/" => `/`
    }
  }

  private[this] val order: Parser[Order] = column~orderType ^^ { case c~t => Order(c, t) }
  private[this] val filter: Parser[Filter] = column~(numFilterType|strFilterType) ^^ { case c~t => Filter(c, t) }
  val expr: Parser[Expr] = column~exprOperation~column~("="~>column) ^^ { case col1~op~col2~result => Expr(col1, col2, result, op) }
}
