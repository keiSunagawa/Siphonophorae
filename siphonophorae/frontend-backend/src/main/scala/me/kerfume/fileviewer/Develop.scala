package me.kerfume.fileviewer

object Develop {
  import scalaz.zio._

  val tbl = Vector(
        Vector("id", "name", "age"),
        Vector("1", "taro", "18"),
        Vector("2", "john", "22"),
        Vector("3", "alice", "20")
  )

  trait PresenterDevelop extends Presenter {
    override val presenter = new Presenter.Service {
      def getTable(): UIO[Table] = IO.effectTotal(tbl)
      def getOrder(): UIO[String] = IO.effectTotal("age asc")
      def getFilter(): UIO[String] = IO.effectTotal("id >= 2")
      def getExpr(): UIO[String] = IO.effectTotal("id + age = newAge")

      def printTable(table: Table): UIO[Unit] = IO.effectTotal(println(table))
      def orderError(errors: Seq[String]): UIO[Unit] = IO.effectTotal(println(errors))
      def filterError(errors: Seq[String]): UIO[Unit] = IO.effectTotal(println(errors))
      def exprError(errors: Seq[String]): UIO[Unit] = IO.effectTotal(println(errors))
    }
  }

  val runtime = new DefaultRuntime {}
  val dPre = new PresenterDevelop {}
  val dev = Module.program.provide(dPre)
}
