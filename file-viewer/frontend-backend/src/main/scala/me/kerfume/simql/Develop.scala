package me.kerfume.simql

import scalaz.zio._
import scalaz.zio.clock._
import scalaz.zio.duration._
import scala.volatile

object Develop3 {
  import freestyle.free._
  import freestyle.free.implicits._

  import cats.implicits._
  import cats.Id
  implicit val outsideHandler = new Outside.Handler[Id] {
    override def fetchSimqlQuery(): Id[String] = "accounts : id"
    override def sendSQL(sql: String): Id[Unit] = println(sql)
  }

  def run = Application.program[Frontend.Op].interpret[Id]
}
// object Develop {
//   import Presenter.PresenterError

//   val runtime = new DefaultRuntime {}
//   var query = "init : id"
//   @volatile
//   var realQueue: List[Int] = Nil
//   // TODO clock
//   val system = Module.system.provide(new PresenterDevelop {})
//   val loop = (for {
//     act <- system
//     _ <- (for {
//       qc <- IO.effectTotal(realQueue)
//       _ <- if (qc.nonEmpty) {
//         println("to send ===")
//         act.send(qc.head)
//       }
//       else IO.effectTotal(())
//       _ = {
//         if (qc.nonEmpty) {
//           println(qc)
//           realQueue = qc.tail
//         }
//        }
//     } yield ()).forever.fork
//   } yield ())
//   runtime.unsafeRunAsync_(loop)

//   def push(): Unit = {
//     realQueue = 1 :: realQueue
//   }

//   trait PresenterDevelop extends Presenter {
//     override val presenter = new Presenter.Service {
//       def getSimqlQuery(): ZIO[Presenter, PresenterError, String] = IO.effect(query)
//       def sendSQL(sql: String): ZIO[Presenter, PresenterError, Unit] = IO.effect(println(sql))
//     }
//   }
// }

// object Develop2 {
//   val a = for {
//     q <- Queue.bounded[Int](10)
//     fb <- (for {
//       i <- q.take
//       _ = println(s"== send and take $i")
//     } yield ()).fork
//     _ <- q.offer(1)
//     _ <- fb.join
//   } yield ()

//   def run = (new DefaultRuntime() {}).unsafeRun(a)
// }
