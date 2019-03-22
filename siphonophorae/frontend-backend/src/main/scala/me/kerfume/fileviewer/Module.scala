package me.kerfume.fileviewer

import scalaz.zio._

object Module {
  import Presenter.Helper._

  import Functions._
  import Parser._

  // TODO Error型が雑…
  val program: ZIO[Presenter, Throwable, Unit] = for {
    table <- getTable()
    orderStr <- getOrder()
    filterStr <- getFilter()
    exprStr <- getExpr()

    filter = decodeFilter(filterStr)
    order = decodeOrder(orderStr)
    expr = decodeExpr(exprStr)

    filteredE = filter.toRight("invalid filter format.").flatMap(procFilter(table, _))
    _ <- filterError(filteredE.left.toOption.toSeq)
    filtered = filteredE.right.getOrElse(table)

    expredE = expr.toRight("invalid expr format.").flatMap(procExpr(filtered, _))
    _ <- exprError(expredE.left.toOption.toSeq)
    expred = expredE.right.getOrElse(filtered)

    orderedE = order.toRight("invalid order format.").flatMap(procOrder(expred, _))
    _ <- orderError(orderedE.left.toOption.toSeq)
    ordered = orderedE.right.getOrElse(expred)

    _ <- printTable(ordered)
  } yield ()
}

trait Presenter {
  def presenter: Presenter.Service
}

object Presenter {
  type PresenterError = Throwable

  trait Service {
    // TODO error type
    def getTable(): UIO[Table]
    def getOrder(): UIO[String]
    def getFilter(): UIO[String]
    def getExpr(): UIO[String]

    def printTable(table: Table): UIO[Unit]
    def orderError(errors: Seq[String]): UIO[Unit]
    def filterError(errors: Seq[String]): UIO[Unit]
    def exprError(errors: Seq[String]): UIO[Unit]
  }

  object Helper {
    def getTable(): ZIO[Presenter, PresenterError, Table] = ZIO.accessM(_.presenter.getTable())
    def getOrder(): ZIO[Presenter, PresenterError, String] = ZIO.accessM(_.presenter.getOrder())
    def getFilter(): ZIO[Presenter, PresenterError, String] = ZIO.accessM(_.presenter.getFilter())
    def getExpr(): ZIO[Presenter, PresenterError, String] = ZIO.accessM(_.presenter.getExpr())

    def printTable(table: Table): ZIO[Presenter, PresenterError, Unit] = ZIO.accessM(_.presenter.printTable(table))
    def orderError(errors: Seq[String]): ZIO[Presenter, PresenterError, Unit] =
      ZIO.accessM(_.presenter.orderError(errors))
    def filterError(errors: Seq[String]): ZIO[Presenter, PresenterError, Unit] =
      ZIO.accessM(_.presenter.filterError(errors))
    def exprError(errors: Seq[String]): ZIO[Presenter, PresenterError, Unit] =
      ZIO.accessM(_.presenter.exprError(errors))
  }
}
