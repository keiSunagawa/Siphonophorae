package me.kerfume.simql

import me.kerfume.simql.node._

package object transpiler {
  type TranspileError = String
  case class ASTMetaData(tables: Seq[SymbolWrapper])
}
