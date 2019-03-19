package me.kerfume.simql.transpiler.resolver

import me.kerfume.simql.transpiler.ASTMetaData
import me.kerfume.simql.node._

trait Resolver {
  def resolve(ast: SimqlRoot, meta: ASTMetaData): Either[String, SimqlRoot]
}
