package me.kerfume.fileviewer

import Command._

trait Command
case class Order(column: String, orderType: OrderType) extends Command
case class Filter(column: String, op: FilterOperator[ColumnType]) extends Command
case class Expr(column1: String, column2: String, result: String, op: ExprOperator) extends Command

object Command {
  sealed trait OrderType
  case object Asc extends OrderType
  case object Desc extends OrderType

  sealed trait ColumnType {
    type AUX
  }
  trait StringC extends ColumnType {
    type AUX = String
  }
  trait NumberC extends ColumnType {
    type AUX = Int
  }

  sealed trait FilterOperator[+CT <: ColumnType] {
    val value: CT#AUX
  }
  case class LT(value: Int) extends FilterOperator[NumberC]
  case class LE(value: Int) extends FilterOperator[NumberC]
  case class GT(value: Int) extends FilterOperator[NumberC]
  case class GE(value: Int) extends FilterOperator[NumberC]
  case class IN(value: String) extends FilterOperator[StringC]
  case class EQ(value: String) extends FilterOperator[StringC]

  sealed trait ExprOperator {
    val calc: (Int, Int) => Int
  }
  case object `+` extends ExprOperator {
    override val calc: (Int, Int) => Int = _ + _
  }
  case object `-` extends ExprOperator{
    override val calc: (Int, Int) => Int = _ - _
  }
  case object `*` extends ExprOperator {
    override val calc: (Int, Int) => Int = _ * _
  }
  case object `/` extends ExprOperator {
    override val calc: (Int, Int) => Int = { (a, b) => if (b == 0) 0 else a / b}
  }
}
