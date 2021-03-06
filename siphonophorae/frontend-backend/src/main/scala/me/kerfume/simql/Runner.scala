package me.kerfume.simql

import cats.free._
import cats.free.Free._
import cats.{~>, Id, InjectK}

object Runner {

  import cats.data.EitherK

  type SimqlApp[A] = EitherK[Presenter.Op, RDB.Op, A]
  def program(implicit I: Presenter.Helper[SimqlApp], D: RDB.Helper[SimqlApp]): Free[SimqlApp, Unit] = {
    import I._
    for {
      simql <- getSimqlQuery()
      _ <- Module.simqlToMysql(simql) match {
            case Right(sql)  => resultTo(sql)
            case Left(error) => printError(error.toString) // FIXME toString...
          }
    } yield ()
  }

  def resultTo(sql: String)(implicit I: Presenter.Helper[SimqlApp], D: RDB.Helper[SimqlApp]): Free[SimqlApp, Unit] = {
    import I._, D._
    for {
      _ <- printSQL(sql)
      _ <- sendSQL(sql)
    } yield ()
  }
}

object Presenter {
  sealed trait Op[A]
  case object GetSimqlQuery extends Op[String]
  case class PrintSQL(sql: String) extends Op[Unit]
  case class PrintError(error: String) extends Op[Unit]

  class Helper[F[_]](implicit I: InjectK[Op, F]) {
    def getSimqlQuery(): Free[F, String] = Free.inject[Op, F](GetSimqlQuery)
    def printSQL(sql: String): Free[F, Unit] = Free.inject[Op, F](PrintSQL(sql))
    def printError(error: String): Free[F, Unit] = Free.inject[Op, F](PrintError(error))
  }
  object Helper {
    implicit def presenterOp[F[_]](implicit I: InjectK[Op, F]): Helper[F] = new Helper[F]
  }
}

object RDB {
  sealed trait Op[A]
  case class SendSQL(sql: String) extends Op[Unit] // TODO response value

  class Helper[F[_]](implicit I: InjectK[Op, F]) {
    def sendSQL(sql: String): Free[F, Unit] = Free.inject[Op, F](SendSQL(sql))
  }
  object Helper {
    implicit def rdbOp[F[_]](implicit I: InjectK[Op, F]): Helper[F] = new Helper[F]
  }
}
