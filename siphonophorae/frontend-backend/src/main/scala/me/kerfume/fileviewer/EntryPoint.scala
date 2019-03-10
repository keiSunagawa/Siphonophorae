package me.kerfume.fileviewer

import scalaz.zio._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{ JSExport, JSExportTopLevel }

@JSExportTopLevel("FileViewerEntryPoint")
object FileViewerEntryPoint {
  @JSExport
  def compile(
    // input
    getTableJs: js.Function0[js.Array[js.Array[String]]],
    getFilterJs: js.Function0[String],
    getExprJs: js.Function0[String],
    getOrderJs: js.Function0[String],
    // output
    printTableJs: js.Function1[js.Array[js.Array[String]], Unit],
    filterErrorJs: js.Function1[String, Unit],
    exprErrorJs: js.Function1[String, Unit],
    orderErrorJs: js.Function1[String, Unit],
  ): Service = {

    trait PresenterLive extends Presenter {
      override val presenter = new Presenter.Service {
        def getTable(): UIO[Table] = IO.effectTotal { getTableJs.apply().map(_.toVector).toVector }
        def getOrder(): UIO[String] = IO.effectTotal { getOrderJs.apply() }
        def getFilter(): UIO[String] = IO.effectTotal { getFilterJs.apply() }
        def getExpr(): UIO[String] = IO.effectTotal { getExprJs.apply() }

        def printTable(table: Table): UIO[Unit] = IO.effectTotal {
          val converted = table.map(_.toJSArray).toJSArray
          printTableJs.apply(converted)
        }
        def orderError(errors: Seq[String]): UIO[Unit] = IO.effectTotal {
          errors.foreach(orderErrorJs.apply)
        }
        def filterError(errors: Seq[String]): UIO[Unit] = IO.effectTotal {
          errors.foreach(filterErrorJs.apply)
        }
        def exprError(errors: Seq[String]): UIO[Unit] = IO.effectTotal {
          errors.foreach(exprErrorJs.apply)
        }
      }
    }
    object PresenterLive extends PresenterLive

    val program = Module.program.provide(PresenterLive)
    new Service(program)
  }

  class Service(
    program: ZIO[Any, Throwable, Unit]
  ) {
    val runtime = new DefaultRuntime {}
    @JSExport
    def run(): Unit = runtime.unsafeRun(program)
  }
}
