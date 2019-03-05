package me.kerfume.fileviewer

import Outside.Table
import Command._

import scala.util.{ Failure, Success, Try }


trait OrderFunctions { self: Functions =>
  def procOrder(table: Table, order: Order): Either[String, Table] = withoutHeader(table) { (header, body) =>
    for {
      index <- columnIndex(header, order.column)
      ordered = orderBy(index, body)
    } yield if (order.orderType == Asc) ordered else ordered.reverse
  }

  // IndexOutOfBoundsException...
  private[this] def orderBy(index: Int, tbl: Table): Table = {
    Try { tbl.sortBy(_(index).toInt) } match {
      case Success(sorted) => sorted
      case Failure(_) => tbl.sortBy(_(index))
    }
  }
}
