package me.kerfume.simql

import cats.~>
import cats.Monad
import monix.execution.Scheduler.Implicits.global
import monix.execution.Cancelable
import monix.reactive._

import concurrent.duration._

trait Application[F[_]] {
  implicit def M: Monad[F]
  def interpreter: Module.SimqlApp ~> F
  def eventStream: Observable[Event]

  lazy val cancelable: Cancelable = eventStream.map {
    case Submit =>
      Module.program.foldMap(interpreter)
  }.subscribe()

  def cleanup() = {
    cancelable.cancel()
  }

  sealed trait Event
  case object Submit extends Event
}
