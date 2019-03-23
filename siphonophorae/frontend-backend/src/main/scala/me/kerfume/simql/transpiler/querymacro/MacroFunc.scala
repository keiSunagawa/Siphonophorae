package me.kerfume.simql.querymacro

import me.kerfume.simql.node._
import me.kerfume.simql.transpiler.TranspileError

trait MacroFunc {
  val key: String
}

abstract class HighSymbolMacro extends MacroFunc {
  def apply0(args: Seq[SymbolWrapper]): Either[TranspileError, HighSymbol]
  def apply(args: Seq[MacroArg]): Either[TranspileError, HighSymbol] = {
    val validArgs = args.collect { case a: SymbolWrapper => a } // TODO allowed string and number
    for {
      _ <- Either.cond(validArgs.length == args.length, (), s"args type mismatch. macro function $$$key.")
      res <- apply0(validArgs)
    } yield res
  }
}

abstract class CondMacro extends MacroFunc {
  def apply0(symbolArgs: Seq[SymbolWrapper], stringArgs: Seq[StringWrapper], numberArgs: Seq[NumberWrapper]): Either[TranspileError, Cond]
  def apply(args: Seq[MacroArg]): Either[TranspileError, Cond] = {
    val symbolArgs = args.collect { case a: SymbolWrapper => a }
    val stringArgs = args.collect { case a: StringWrapper => a }
    val numberArgs = args.collect { case a: NumberWrapper => a }
    val validArgs = symbolArgs ++ stringArgs ++ numberArgs
    for {
      _ <- Either.cond(validArgs.length == args.length, (), s"args type mismatch. macro function $$$key.")
      res <- apply0(symbolArgs, stringArgs, numberArgs)
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
    def condMacros: Map[String, CondMacro] =
      fs.collect {
        case m: CondMacro =>
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
  case object Like extends CondMacro {
    val key: String = "like"
    def apply0(symbolArgs: Seq[SymbolWrapper], stringArgs: Seq[StringWrapper], numberArgs: Seq[NumberWrapper]): Either[TranspileError, Cond] = {
      if (symbolArgs.length == 1 && stringArgs.length == 1 && numberArgs.isEmpty) {
        val col = symbolArgs.head.label
        val str = stringArgs.head.value.replaceAll("\"", "")
        Right(Raw(s"${col} LIKE('%${str}%')")) // TODO deffered resolve symbolWrapper
      } else Left(s"invalid args. macro function $$$key.")
    }
  }
}
