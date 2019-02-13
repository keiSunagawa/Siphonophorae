package me.kerfume

import me.kerfume.Outside.Table
import Command._

import scala.util.{ Failure, Success, Try }

trait FilterFunctions { self: Functions =>
  def procFilter(table: Table, filter: Filter): Either[String, Table] = withoutHeader(table) { (header, body) =>
    val Filter(c, op) = filter
    val index = columnIndex(header, c)
    op match {// TODO もうちょい共通化できそう
      case LT(n) =>
        val cond: Int => Boolean = { _ < n }
        for {
          i <- index
          filtered <- filterNumberColumn(body, i, cond)
        } yield filtered
      case LE(n) =>
        val cond: Int => Boolean = { _ <= n }
        for {
          i <- index
          filtered <- filterNumberColumn(body, i, cond)
        } yield filtered
      case GT(n) =>
        val cond: Int => Boolean = { _ > n }
        for {
          i <- index
          filtered <- filterNumberColumn(body, i, cond)
        } yield filtered
      case GE(n) =>
        val cond: Int => Boolean = { _ >= n }
        for {
          i <- index
          filtered <- filterNumberColumn(body, i, cond)
        } yield filtered
      case IN(s) =>
        val cond: String => Boolean = { _.contains(s) }
        for {
          i <- index
          filtered <- filterStringColumn(body, i, cond)
        } yield filtered
      case EQ(s) =>
        val cond: String => Boolean = { _ == s }
        for {
          i <- index
          filtered <- filterStringColumn(body, i, cond)
        } yield filtered
    }
  }

  protected[this] def filterNumberColumn(table: Table, index: Int, cond: Int => Boolean): Either[String, Table] = {
    Try { table.filter(line => cond(line(index).toInt)) } match {
      case Success(value) => Right(value)
      case Failure(e) => Left(s"failed filter. column index: $index, ${e.getMessage}")
    }
  }

  protected[this] def filterStringColumn(table: Table, index: Int, cond: String => Boolean): Either[String, Table] = {
    Try { table.filter(line => cond(line(index))) } match {
      case Success(value) => Right(value)
      case Failure(e) => Left(s"failed filter. column index: $index, ${e.getMessage}")
    }
  }
}
