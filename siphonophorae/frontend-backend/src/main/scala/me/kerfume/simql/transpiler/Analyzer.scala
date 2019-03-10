package me.kerfume.simql.transpiler

import me.kerfume.simql.node.SimqlRoot

object Analyzer {
  def analyze(ast: SimqlRoot): ASTMetaData = {
    val tables = ast.from.lhs +: ast.from.rhss.map(_.rhsTable)
    ASTMetaData(
      tables = tables
    )
  }
}
