package io.m4iraki
package domain

import java.time.Instant

opaque type UUID = String

object UUID {
  def make: UUID = java.util.UUID.randomUUID().toString
}

opaque type Millis = Long

object Millis {
  def of(long: Long): Millis = long
  def now: Millis = System.currentTimeMillis()
  given Ordering[Millis] = Ordering.Long

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
