package me.kerfume

import cats.free._
import cats.free.Free._
import cats.{ Id, ~> }
import cats.arrow.FunctionK

object TableMakeServiceDevelop {
  import TableMakeService._

  def devCompiler(filter: String, expr: String, order: String): Outside ~> Id = new FunctionK[Outside, Id] {
    override def apply[A](fa: Outside[A]): Id[A] = fa match {
      case GetTable => Vector(
        Vector("id", "name", "age"),
        Vector("1", "taro", "18"),
        Vector("2", "john", "22"),
        Vector("3", "alice", "20")
      )
      case GetFilter => filter
      case GetExpr => expr
      case GetOrder => order

      case DoNothing =>
        ()
      case PrintTable(tbl) =>
        println(tbl)
        ()
      case FilterError(msg) =>
        println(msg)
        ()
      case ExprError(msg) =>
        println(msg)
        ()
      case OrderError(msg) =>
        println(msg)
        ()
    }
  }

  def run(filter: String, expr: String, order: String) = makeService.foldMap(devCompiler(filter, expr, order))
}
