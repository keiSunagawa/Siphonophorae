package me.kerfume.fileviewer

object Functions extends Functions with FilterFunctions with OrderFunctions with ExprFunctions

trait Functions {
  protected[this] def columnIndex(header: Header, columnName: String): Either[String, Int] = {
    val index = header.indexOf(columnName)
    if (index >= 0) Right(index)
    else Left("column not found.")
  }

  protected[this] def withoutHeader(tbl: Table)(f: (Header, Table) => Either[String, Table]): Either[String, Table] = {
    val h +: t = tbl
    val ft = f(h, t)
    ft.map(h +: _)
  }
}
