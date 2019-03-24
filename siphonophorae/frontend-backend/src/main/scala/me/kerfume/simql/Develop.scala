package me.kerfume.simql

import me.kerfume.simql.transpiler.Module._
import me.kerfume.simql.transpiler.parser.Parser

object Develop {
  def run(query: String): Either[String, String] = simqlToMysql(query)
}

object MacroTest {
  import  me.kerfume.simql.transpiler.querymacro.MacroFuncGenerator

  def run(qq: String) = {
    val in = s"""defun f(a: Symbol) => Symbol = {
                |  ${qq}
                |}
              """.stripMargin
    val ast = Parser.parse(Parser.macroFunc, in).get
    MacroFuncGenerator.generate(ast)
  }
}
// import cats.{Id, InjectK, ~>, Monad}
// import cats.arrow.FunctionK
// import cats.catsInstancesForId

// object CallBacker {
//   private var onNext: () => Unit = () => ()
//   private var onComplete: () => Unit = () => ()

//   def setCallBack(n: () => Unit, s: () => Unit) = {
//     onNext = n
//     onComplete = s
//   }
//   def next: Unit = onNext()
//   def stop: Unit = onComplete()
// }

// object Develop {
//   val presenter = new FunctionK[Presenter.Op, Id] {
//     import Presenter._
//     def apply[A](op: Op[A]) = op match {
//       case GetSimqlQuery => "accounts : id"
//       case PrintSQL(sql) => println(sql)
//       case PrintError(error) => println(error)
//     }
//   }

//   val rdb = new FunctionK[RDB.Op, Id] {
//     import RDB._
//     def apply[A](op: Op[A]) = op match {
//       case SendSQL(sql) =>
//         println(s"send to... $sql")
//     }
//   }

//   val app = new Application[Id] {
//     import monix.execution.Cancelable
//     import monix.reactive._

//     implicit val M: Monad[Id] = catsInstancesForId
//     val interpreter: Module.SimqlApp ~> Id = presenter or rdb
//     val eventStream = Observable.unsafeCreate[Application.Event] { s =>
//       CallBacker.setCallBack(
//         () => s.onNext(Application.Submit),
//         () => s.onComplete()
//       )
//       Cancelable(() => ())
//     }

//     // init load
//     cancelable
//   }
// }

// object Interactive {
//   implicit val M: Monad[Id] = catsInstancesForId

//   def interactivePresenter(in: String) = new FunctionK[Presenter.Op, Id] {
//     import Presenter._
//     def apply[A](op: Op[A]) = op match {
//       case GetSimqlQuery => in
//       case PrintSQL(sql) => println(sql)
//       case PrintError(error) => println(error)
//     }
//   }
//   def interpreter(simql: String): Module.SimqlApp ~> Id = interactivePresenter(simql) or Develop.rdb

//   def run(simql: String) = Module.program.foldMap(interpreter(simql))
// }
