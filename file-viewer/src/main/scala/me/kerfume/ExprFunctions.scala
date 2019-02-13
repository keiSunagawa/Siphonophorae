package me.kerfume

import me.kerfume.Outside.Table

import scala.util.{ Failure, Success, Try }

trait ExprFunctions { self: Functions =>
  def procExpr(table: Table, expr: Expr): Either[String, Table] = {
    val header +: body = table
    for {
      index1 <- columnIndex(header, expr.column1)
      index2 <- columnIndex(header, expr.column2)
      newBody <- runCalc(body, index1, index2, expr.op.calc)
      newHeader = header :+ expr.result
    } yield newHeader +: newBody
  }

  private[this] def runCalc(tbl: Table, i1: Int, i2: Int, f: (Int, Int) => Int): Either[String, Table] = Try {
    tbl.map { line =>
      val res = f(line(i1).toInt, line(i2).toInt)
      line :+ res.toString
    }
  } match {
    case Success(v) => Right(v)
    case Failure(_) => Left("invalid expr target.")
  }
}
