package me.kerfume

import cats.free._
import cats.free.Free._
import me.kerfume.Outside.Table

sealed trait Outside[A]
case object GetTable extends Outside[Table]
case object GetOrder extends Outside[String]
case object GetFilter extends Outside[String]
case object GetExpr extends Outside[String]
case object DoNothing extends Outside[Unit] // これもうちょいいい書き方あるかも？

case class PrintTable(table: Table) extends Outside[Unit]
case class OrderError(msg: String) extends Outside[Unit]
case class FilterError(msg: String) extends Outside[Unit]
case class ExprError(msg: String) extends Outside[Unit]

import me.kerfume.Command._

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

object Outside {
  type Table = Vector[Vector[String]]
  type Header = Vector[String]
  type OutSideF[A] = Free[Outside, A]

  def doNothing() = liftF(DoNothing)
  def getTable() = liftF(GetTable)
  def getOrder() = liftF(GetOrder)
  def getFilter() = liftF(GetFilter)
  def getExpr() = liftF(GetExpr)

  def printTable(table: Table) = liftF(PrintTable(table))
  def orderError(res:  Either[String, _]) = res match {
    case Left(msg) => liftF(OrderError(msg))
    case Right(_) => doNothing()
  }
  def filterError(res: Either[String, _]) = res match {
    case Left(msg) => liftF(FilterError(msg))
    case Right(_) => doNothing()
  }
  def exprError(res: Either[String, _]) = res match {
    case Left(msg) => liftF(ExprError(msg))
    case Right(_) => doNothing()
  }
}
