package me.kerfume.negligentquery

import cats.free.Free
import cats.free.Free.liftF


sealed trait Outside[A]
case object GetNegligentQuery extends Outside[String]
case class PrintSQL(sql: String) extends Outside[Unit]

object Outside {
  type OutsideF[A] = Free[Outside, A]

  def getNegligentQuery(): OutsideF[String] = liftF(GetNegligentQuery)
  def printSQL(sql: String): OutsideF[Unit] = liftF(PrintSQL(sql))
}
