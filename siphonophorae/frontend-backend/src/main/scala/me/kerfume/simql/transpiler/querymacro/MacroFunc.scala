package me.kerfume.simql.querymacro

import me.kerfume.simql.node._
import me.kerfume.simql.transpiler.TranspileError

trait MacroFunc
abstract class HighSymbolMacro extends MacroFunc {
  val key: String
  def apply0(args: Seq[SymbolWrapper]): Either[TranspileError, HighSymbol]
  def apply(args: Seq[MacroArg]): Either[TranspileError, HighSymbol] = {
    val validArgs = args.collect { case a: SymbolWrapper => a }
    for {
      _ <- Either.cond(validArgs.length == args.length, (), s"args type mismatch. macro function $$$key.")
      res <- apply0(validArgs)
    } yield res
  }
}

object MacroFunc {
  implicit class FuncsOps(fs: Seq[MacroFunc]) {
    def highSymbolMacros: Map[String, HighSymbolMacro] =
      fs.collect {
        case m: HighSymbolMacro =>
          m.key -> m
      }(collection.breakOut)
  }
}

object buildin {
  case object Count extends HighSymbolMacro {
    val key: String = "c"
    def apply0(args: Seq[SymbolWrapper]): Either[TranspileError, HighSymbol] = {
      if (args.length <= 1) {
        val col = args.headOption.map(_.label).getOrElse("1")
        Right(Raw(s"COUNT(${col})")) // TODO deffered resolve symbolWrapper
      } else Left(s"invalid args. macro function $$$key.")
    }
  }
}
