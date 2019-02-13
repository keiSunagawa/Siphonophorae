package me.kerfume


import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSExport, ScalaJSDefined }

@ScalaJSDefined
@JSExport("TableMakeService")
object TableMakeService extends js.Object {
  import Outside._
  import Functions._
  import Parser._

  val makeService: OutSideF[Unit] = for {
    table <- getTable()
    orderStr <- getOrder()
    filterStr <- getFilter()
    exprStr <- getExpr()

    filter = decodeFilter(filterStr)
    order = decodeOrder(orderStr)
    expr = decodeExpr(exprStr)

    filteredE = filter.toRight("invalid filter format.").flatMap(procFilter(table, _))
    _ <- filterError(filteredE)
    filtered = filteredE.right.getOrElse(table)

    expredE = expr.toRight("invalid order format.").flatMap(procExpr(filtered, _))
    _ <- exprError(expredE)
    expred = expredE.right.getOrElse(filtered)

    orderedE = order.toRight("invalid order format.").flatMap(procOrder(expred, _))
    _ <- orderError(orderedE)
    ordered = orderedE.right.getOrElse(expred)

    _ <- printTable(expred)
  } yield ()
}
