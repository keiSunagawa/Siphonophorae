package me.kerfume.simql

import me.kerfume.simql.node._
import me.kerfume.simql.querymacro._

package object transpiler {
  type TranspileError = String
  // ただのDTOであるべき...?
  case class ASTMetaData(tables: Seq[SymbolWrapper], macroFuncs: Seq[MacroFunc] = Nil)
}
