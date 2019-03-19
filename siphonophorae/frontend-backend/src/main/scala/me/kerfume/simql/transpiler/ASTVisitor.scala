package me.kerfume.simql.transpiler

import me.kerfume.simql.node._

trait ASTVisitor {
  def visit(node: StringWrapper): StringWrapper = node
  def visit(node: NumberWrapper): NumberWrapper = node
  def visit(node: SymbolWrapper): SymbolWrapper = node
  def visit(node: SymbolWithAccessor): SymbolWithAccessor = node

  def visit(node: Term): Term = node match {
    case n: StringWrapper => visit(n)
    case n: NumberWrapper => visit(n)
    case n: SymbolWrapper => visit(n)
    case n: SymbolWithAccessor => visit(n)
    case NullLit => NullLit
  }

  def visit(node: BinaryCond): BinaryCond = {
    node.copy(
      lhs = visit(node.lhs),
      rhs = visit(node.rhs)
    )
  }
  def visit(node: IsNull): IsNull = {
    node.copy(
      lhs = visit(node.lhs)
    )
  }
  def visit(node: IsNotNull): IsNotNull = {
    node.copy(
      lhs = visit(node.lhs)
    )
  }

  def visit(node: Cond): Cond = node match {
    case n: BinaryCond => visit(n)
    case n: IsNull => visit(n)
    case n: IsNotNull => visit(n)
  }

  def visit(node: ExprRhs): ExprRhs = {
    node.copy(
      value = visit(node.value)
    )
  }

  def visit(node: Expr): Expr = {
    val rhss = node.rhss.map(visit)
    node.copy(
      lhs = visit(node.lhs),
      rhss = rhss
    )
  }

  def visit(node: Join): Join = {
    node.copy(
      rhsTable = visit(node.rhsTable),
      on = visit(node.on)
    )
  }

  def visit(node: From): From = {
    val rhss = node.rhss.map(visit)
    node.copy(
      lhs = node.lhs,
      rhss = rhss
    )
  }

  def visit(node: Select): Select = {
    node.copy(
      values = node.values.map(visit)
    )
  }

  def visit(node: Where): Where = {
    node.copy(
      value = visit(node.value)
    )
  }

  def visit(node: LimitOffset): LimitOffset = {
    node.copy(
      limit = visit(node.limit),
      offset = node.offset.map(visit)
    )
  }

  def visit(node: Order): Order = {
    val tail = node.tail.map(visit)
    node.copy(
      head = visit(node.head),
      tail = tail
    )
  }

  def visit(node: SimqlRoot): SimqlRoot = {
    node.copy(
      from = visit(node.from),
      select = node.select.map(visit),
      where = node.where.map(visit),
      limitOffset = node.limitOffset.map(visit),
      order = node.order.map(visit)
    )
  }
}
