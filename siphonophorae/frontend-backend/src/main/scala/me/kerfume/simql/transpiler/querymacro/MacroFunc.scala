package me.kerfume.simql.querymacro

import me.kerfume.simql.node._
import me.kerfume.simql.transpiler.TranspileError

trait MacroFunc {
  val key: String
}

abstract class HighSymbolMacro extends MacroFunc {
  def apply0(args: Seq[SymbolWithAccessor]): Either[TranspileError, HighSymbol]
  def apply(args: Seq[MacroArg]): Either[TranspileError, HighSymbol] = {
    val validArgs = args.collect { case a: SymbolWithAccessor => a } // TODO allowed string and number
    for {
      _ <- Either.cond(validArgs.length == args.length, (), s"args type mismatch. macro function $$$key.")
      res <- apply0(validArgs)
    } yield res
  }
}

abstract class CondMacro extends MacroFunc {
  def apply0(
    symbolArgs: Seq[SymbolWithAccessor],
    stringArgs: Seq[StringWrapper],
    numberArgs: Seq[NumberWrapper]
  ): Either[TranspileError, Cond]
  def apply(args: Seq[MacroArg]): Either[TranspileError, Cond] = {
    val symbolArgs = args.collect { case a: SymbolWithAccessor => a }
    val stringArgs = args.collect { case a: StringWrapper      => a }
    val numberArgs = args.collect { case a: NumberWrapper      => a }
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
    def apply0(args: Seq[SymbolWithAccessor]): Either[TranspileError, HighSymbol] = {
      if (args.length <= 1) {
        val col: Term = args.headOption.getOrElse(Raw("1"))
        Right(Raw(s"COUNT(?)", List(col)))
      } else Left(s"invalid args. macro function $$$key.")
    }
  }
  case object Like extends CondMacro {
    val key: String = "like"
    def apply0(
      symbolArgs: Seq[SymbolWithAccessor],
      stringArgs: Seq[StringWrapper],
      numberArgs: Seq[NumberWrapper]
    ): Either[TranspileError, Cond] = {
      if (symbolArgs.length == 1 && stringArgs.length == 1 && numberArgs.isEmpty) {
        val col = symbolArgs.head
        val str = stringArgs.head
        val likeStr = str.copy(
          value = s"%${str.value}%"
        )
        Right(Raw(s"? LIKE(?)", List(col, likeStr)))
      } else Left(s"invalid args. macro function $$$key.")
    }
  }
}
