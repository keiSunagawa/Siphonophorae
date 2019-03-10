package me.kerfume.simql.transpiler

import me.kerfume.simql.node._

trait Checker {
  def check(ast: SimqlRoot): Either[String, Unit]
}
