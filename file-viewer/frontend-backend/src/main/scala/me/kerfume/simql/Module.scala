package me.kerfume.simql

import scalaz.zio._
import scalaz.zio.clock.Clock
import scalaz.zio.duration._

// object Module {
//   import Parser._
//   import Presenter.PresenterError
//   import Presenter.Helper._

//   // private[this] val trigger: UIO[Queue[Unit]] = Queue.unbounded
//   // val start: UIO[Unit] = for {
//   //   q <- trigger
//   //   _ <- q.offer(())
//   // } yield ()
//   abstract class IOActor {
//     val qq: Queue[Int]
//     def send(i: Int): UIO[Unit]
//     def join(): Task[Unit]
//   }

//   val system: ZIO[Presenter, Nothing, IOActor] = for {
//     q <- Queue.bounded[Int](100)
//     _ = println("== run ===")
//     fiber <- (for {
//       i <- q.take
//       _ = println(s"== take == $i=")
//       query <- getSimqlQuery()
//       ast = paeseSimql(query)
//       _ <- sendSQL(ast.toString)
//     } yield ()).forever.fork
//   } yield new IOActor {
//     val qq = q
//     override def send(i: Int): UIO[Unit] = for {
//       _ <- q.offer(i)
//     } yield ()
//     override def join(): Task[Unit] = for {
//       _ <- fiber.join
//     } yield ()
//   }
// }

trait Presenter {
  def presenter: Presenter.Service
}

object Presenter {
  type PresenterError = Throwable
  trait Service {
    def getSimqlQuery(): ZIO[Presenter, PresenterError, String]

    def sendSQL(sql: String): ZIO[Presenter, PresenterError, Unit]
  }

  object Helper {
    def getSimqlQuery(): ZIO[Presenter, PresenterError, String] = ZIO.accessM(_.presenter.getSimqlQuery())
    def sendSQL(sql: String): ZIO[Presenter, PresenterError, Unit] = ZIO.accessM(_.presenter.sendSQL(sql))
  }
}

import freestyle.free._
import freestyle.free.implicits._


@module trait Frontend {
  val outside: Outside
}

@free trait Outside {
  def fetchSimqlQuery(): FS[String]
  def sendSQL(sql: String): FS[Unit]
}

object Application {
  def program[F[_]](implicit front: Frontend[F]): FreeS[F, Unit] = {
    import front.outside._
    for {
      q <- fetchSimqlQuery()
      _ <- sendSQL(q)
    } yield ()
  }
}
