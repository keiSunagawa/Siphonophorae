package me.kerfume.simql.transpiler

import me.kerfume.simql.node._

trait Normalizer {
  def normalize(ast: SimqlRoot): SimqlRoot
}
