package io.m4iraki
package domain

import java.time.Instant
import scala.util.Try

opaque type UUID = String

object UUID {
  def make: UUID = java.util.UUID.randomUUID().toString

  def fromString(string: String): Either[String, UUID] =
    Try(
      java.util.UUID.fromString(string).toString,
    ).toEither.left.map(_.getMessage)

  extension (uuid: UUID) {
    def asString: String = uuid
  }

}

opaque type Millis = Long

object Millis {
  def of(long: Long): Millis = long
  def now: Millis = System.currentTimeMillis()
  given Ordering[Millis] = Ordering.Long

  extension (millis: Millis) {
    def long: Long = millis
  }

  extension (lhs: Millis) {
    def +(rhs: Millis): Millis = lhs + rhs
    def -(rhs: Millis): Millis = lhs - rhs
    def >(rhs: Millis): Boolean = lhs > rhs
    def <(rhs: Millis): Boolean = lhs < rhs
    def >=(rhs: Millis): Boolean = lhs >= rhs
    def <=(rhs: Millis): Boolean = lhs <= rhs
    def pretty: String = Instant.ofEpochMilli(lhs).toString
  }

}
