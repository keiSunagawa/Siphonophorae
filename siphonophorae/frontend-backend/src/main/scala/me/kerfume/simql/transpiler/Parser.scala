package me.kerfume.simql.transpiler

import scala.util.parsing.combinator.JavaTokenParsers
import me.kerfume.simql.node._

object Parser extends JavaTokenParsers {
  def parseSimql(code: String): Option[SimqlRoot] = parse(simql, code) match {
    case Success(root, a) if (a.atEnd) => Some(root)
    case e =>
      println(s"parse failed. reason: $e")
      None
  }

  val string: Parser[StringWrapper] = stringLiteral ^^ { StringWrapper(_) }
  val number: Parser[NumberWrapper] = decimalNumber ^^ { s => NumberWrapper(BigDecimal(s)) }
  val symbol: Parser[SymbolWrapper] = """[a-zA-Z][a-zA-Z0-9_]*""".r ^^ { SymbolWrapper(_) }
  val accessor: Parser[Accessor] = """\$[0-9]""".r ^^ { case s => Accessor(s.tail.toInt) }
  val symbolWithAccessor: Parser[SymbolWithAccessor] = opt(accessor~".")~symbol ^^ { case acOpt~s =>
    val accessor = acOpt.map { case ac~_ => ac }
    SymbolWithAccessor(s, accessor)
  }
  val term: Parser[Term] = symbolWithAccessor | string | number

  val binaryOp: Parser[BinaryOp] = """(>=|<=|>|<|=|<>|in)""".r ^^ { case opStr =>
    val op = opStr match {
      case ">" => BinaryOp.GT
      case "<" => BinaryOp.LT
      case ">=" => BinaryOp.GE
      case "<=" => BinaryOp.LE
      case "=" => BinaryOp.EQ
      case "<>" => BinaryOp.NE
      // case "in" => BinaryOp.IN
      // case "like" => BinaryOp.LIKE
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
  val select = ":"~symbolWithAccessor~rep(symbolWithAccessor) ^^ { case _~col1~cols =>
    Select(col1 +: cols)
  }
  val where = "?"~expr ^^ { case _~exp =>
    Where(exp)
  }
  val limitOffset = "@"~number~opt("-"~number) ^^ { case _~limit~offsetSyntax =>
    val offset = offsetSyntax.map { case _~ofs => ofs }
    LimitOffset(limit, offset)
  }

  val simql = from~opt(select)~opt(where)~opt(limitOffset) ^^ { case f~s~w~l =>
    SimqlRoot(f, s, w, l)
  }
}
