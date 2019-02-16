package me.kerfume

import cats.arrow.FunctionK
import cats.{ Id, ~> }

import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSExport, JSExportTopLevel, ScalaJSDefined }
import js.JSConverters._

@JSExportTopLevel("EntryPoint")
object EntryPoint {
  @JSExport
  def compile(
    // input
    getTable: js.Function0[js.Array[js.Array[String]]],
    getFilter: js.Function0[String],
    getExpr: js.Function0[String],
    getOrder: js.Function0[String],
    // output
    printTable: js.Function1[js.Array[js.Array[String]], Unit],
    filterError: js.Function1[String, Unit],
    exprError: js.Function1[String, Unit],
    orderError: js.Function1[String, Unit],
  ): Service = {
    val compiler = new FunctionK[Outside, Id] {
      override def apply[A](fa: Outside[A]): Id[A] = fa match {
        case GetTable => getTable.apply().map(_.toVector).toVector
        case GetFilter => getFilter.apply()
        case GetExpr => getExpr.apply()
        case GetOrder => getOrder.apply()

        case DoNothing =>
          ()

        case PrintTable(tbl) =>
          val converted = tbl.map(_.toJSArray).toJSArray
          printTable.apply(converted)

        case FilterError(msg) => filterError.apply(msg)
        case ExprError(msg) => exprError.apply(msg)
        case OrderError(msg) => orderError.apply(msg)
      }
    }

    new Service(compiler)
  }

  class Service(
    compiler: Outside ~> Id
  ) {
    @JSExport
    def run(): Unit = TableMakeService.makeService.foldMap(compiler)
  }
}
