package me.kerfume.simql

import scala.util.parsing.combinator.JavaTokenParsers

import node._

object Parser extends JavaTokenParsers {
  val string: Parser[StringWrapper] = stringLiteral ^^ { StringWrapper(_) }
  val number: Parser[NumberWrapper] = decimalNumber ^^ { s => NumberWrapper(BigDecimal(s)) }
  val symbol: Parser[SymbolWrapper] = """[a-zA-Z][a-zA-Z0-9_]*""".r ^^ { SymbolWrapper(_) }
  val term: Parser[Term] = string | number | symbol

  val binaryOp: Parser[BinaryOp] = """(>=|<=|>|<|=|<>|in)""".r ^^ { case opStr =>
    val op = opStr match {
      case ">" => BinaryOp.GT
      case "<" => BinaryOp.LT
      case ">=" => BinaryOp.GE
      case "<=" => BinaryOp.LE
      case "=" => BinaryOp.EQ
      case "<>" => BinaryOp.NE
      case "in" => BinaryOp.IN // inはちょっとダサい
    }
    BinaryOp(op)
  }
  val binaryCond = term~binaryOp~term ^^ { case lhs~op~rhs => BinaryCond(op, lhs, rhs) }

  val logicalOp = """(&&|\|\|)""".r ^^ { case opStr =>
    val op = opStr match {
      case "&&" => LogicalOp.And
      case "||" => LogicalOp.Or
    }
    LogicalOp(op)
  }

  val expr: Parser[Expr] = binaryCond~rep(logicalOp~binaryCond) ^^ { case b~body =>
    val rhss = body.map { case lo~bc => ExprRhs(lo, bc) }
    Expr(b, rhss)
  }

  val joinType = """<<|><""".r ^^ { case jtStr =>
    val jt = jtStr match {
      case "<<" => JoinType.LeftJoin
      case "><" => JoinType.InnerJoin
    }
    JoinType(jt)
  }
  val join = joinType~symbol~"?"~expr ^^ { case jt~rightTable~_~exp =>
    Join(jt, rightTable, exp)
  }

  val from = symbol~rep(join) ^^ { case table~joins =>
    From(table, joins)
  }
  val select = ":"~symbol~rep(symbol) ^^ { case _~col1~cols =>
    Select(col1 +: cols)
  }
  val where = "?"~expr ^^ { case _~exp =>
    Where(exp)
  }

  val simql = from~opt(select)~opt(where) ^^ { case f~s~w =>
    Root(f, s, w)
  }
}
