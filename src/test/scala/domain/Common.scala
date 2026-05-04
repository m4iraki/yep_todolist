package io.m4iraki
package domain

import zio.*
import zio.test.*
import zio.test.Assertion.*

object CommonSpec extends ZIOSpecDefault {

  def spec = suite("Simple types")(
    test("Millis.Arithmetic.addition") {
      val gen = for {
        l1 <- Gen.long(0, 1_000_000_000_000_000L)
        l2 <- Gen.long(0, 1_000_000_000_000_000L)
      } yield (l1, l2)
      check(gen) {
        case (l1, l2) =>
          val m1 = Millis.of(l1)
          val m2 = Millis.of(l2)
          val lsum = l1 + l2
          val msum = m1 + m2
          assertTrue(lsum == msum.long) &&
          assertTrue(Millis.of(lsum) == msum)
      }
    },
    test("Millis.Arithmetic.subtraction") {
      val gen = for {
        l1 <- Gen.long(0, 1_000_000_000_000_000L)
        l2 <- Gen.long(0, 1_000_000_000_000_000L)
      } yield (l1, l2)
      check(gen) {
        case (l1, l2) =>
          val m1 = Millis.of(l1)
          val m2 = Millis.of(l2)
          val lsub = l1 - l2
          val msub = m1 - m2
          assertTrue(lsub == msub.long) &&
          assertTrue(Millis.of(lsub) == msub)
      }
    },
    test("UUID.fromString: should create UUID from valid string") {
      val valid = java.util.UUID.randomUUID().toString
      val uuid = UUID.fromString(valid)
      assertTrue(uuid.contains(valid))
    },
    test("UUID.fromString: should fail on invalid string") {
      val invalid = "hehe"
      val uuid = UUID.fromString(invalid)
      assertTrue(uuid.isLeft)
    },
  )

}
