package me.kerfume.simql.transpiler

import me.kerfume.simql.node._
import me.kerfume.simql.functions._
import cats.instances.list._

trait ASTVisitor {
  def visit(node: StringWrapper): Either[TranspileError, StringWrapper] = Right(node)
  def visit(node: NumberWrapper): Either[TranspileError, NumberWrapper] = Right(node)
  def visit(node: SymbolWrapper): Either[TranspileError, SymbolWrapper] = Right(node)
  def visit(node: SymbolWithAccessor): Either[TranspileError, SymbolWithAccessor] = Right(node)

  def visit(node: Term): Either[TranspileError, Term] = node match {
    case n: StringWrapper => visit(n)
    case n: NumberWrapper => visit(n)
    case n: SymbolWrapper => visit(n)
    case n: SymbolWithAccessor => visit(n)
    case NullLit => Right(NullLit)
  }

  def visit(node: BinaryCond): Either[TranspileError, BinaryCond] = {
    for {
      lhs <- visit(node.lhs)
      rhs <- visit(node.rhs)
    } yield node.copy(
      lhs = lhs,
      rhs = rhs
    )
  }
  def visit(node: IsNull): Either[TranspileError, IsNull] = {
    for {
      lhs <- visit(node.lhs)
    } yield node.copy(
      lhs = lhs
    )
  }
  def visit(node: IsNotNull): Either[TranspileError, IsNotNull] = {
    for {
      lhs <- visit(node.lhs)
    } yield node.copy(
      lhs = lhs
    )
  }

  def visit(node: Cond): Either[TranspileError, Cond] = node match {
    case n: BinaryCond => visit(n)
    case n: IsNull => visit(n)
    case n: IsNotNull => visit(n)
  }

  def visit(node: ExprRhs): Either[TranspileError, ExprRhs] = {
    for {
      value <- visit(node.value)
    } yield node.copy(
      value = value
    )
  }

  def visit(node: Expr): Either[TranspileError, Expr] = {
    for {
      lhs <- visit(node.lhs)
      rhss <- node.rhss.mapE(visit)
    } yield node.copy(
      lhs = lhs,
      rhss = rhss
    )
  }

  def visit(node: Join): Either[TranspileError, Join] = {
    for {
      rhsTable <- visit(node.rhsTable)
      on <- visit(node.on)
    } yield node.copy(
      rhsTable = rhsTable,
      on = on
    )
  }

  def visit(node: From): Either[TranspileError, From] = {
    for {
      lhs <- visit(node.lhs)
      rhss <- node.rhss.mapE(visit)
    } yield node.copy(
      lhs = node.lhs,
      rhss = rhss
    )
  }

  def visit(node: Select): Either[TranspileError, Select] = {
    for {
      values <- node.values.mapE(visit)
    } yield node.copy(
      values = values
    )
  }

  def visit(node: Where): Either[TranspileError, Where] = {
    for {
      value <- visit(node.value)
    } yield node.copy(
      value = value
    )
  }

  def visit(node: LimitOffset): Either[TranspileError, LimitOffset] = {
    for {
      limit <- visit(node.limit)
      offset <- transpose { node.offset.map(visit) }
    } yield node.copy(
      limit = limit,
      offset = offset
    )
  }

  def visit(node: Order): Either[TranspileError, Order] = {
    for {
      head <- visit(node.head)
      tail <- node.tail.mapE(visit)
    } yield node.copy(
      head = head,
      tail = tail
    )
  }

  def visit(node: SimqlRoot): Either[TranspileError, SimqlRoot] = {
    for {
      from <- visit(node.from)
      select <- transpose { node.select.map(visit) }
      where <- transpose { node.where.map(visit) }
      limitOffset <- transpose { node.limitOffset.map(visit) }
      order <- transpose { node.order.map(visit) }
    } yield node.copy(
      from = from,
      select = select,
      where = where,
      limitOffset = limitOffset,
      order = order
    )
  }
}
