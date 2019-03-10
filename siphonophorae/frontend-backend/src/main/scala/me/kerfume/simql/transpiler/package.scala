package me.kerfume.simql

import me.kerfume.simql.node._

package object transpiler {
  case class ASTMetaData(
    tables: Seq[SymbolWrapper]
  )
}
