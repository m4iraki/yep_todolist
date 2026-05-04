package io.m4iraki
package domain

import zio.*
import zio.test.*
import zio.test.Assertion.*

object CommonSpec extends ZIOSpecDefault {

  val genUUID: Gen[Any, UUID] = Gen.uuid.map(
    uuid => UUID.fromString(uuid.toString),
  ).collect {
    case Right(value) => value
  }

  val genMillis: Gen[Any, Millis] =
    Gen.long(0, 1_000_000_000_000_000L).map(Millis.of)

  def spec = suite("Simple types")(
    test("Millis.Arithmetic.addition") {
      val gen = for {
        m1 <- genMillis
        m2 <- genMillis
      } yield (m1, m1)
      check(gen) {
        case (m1, m2) =>
          val lsum = m1.long + m2.long
          val msum = m1 + m2
          assertTrue(lsum == msum.long) &&
          assertTrue(Millis.of(lsum) == msum)
      }
    },
    test("Millis.Arithmetic.subtraction") {
      val gen = for {
        m1 <- genMillis
        m2 <- genMillis
      } yield (m1, m2)
      check(gen) {
        case (m1, m2) =>
          val lsub = m1.long - m2.long
          val msub = m1 - m2
          assertTrue(lsub == msub.long) &&
          assertTrue(Millis.of(lsub) == msub)
      }
    },
    test("UUID.fromString: should create UUID from valid string") {
      check(Gen.uuid){ generated =>
        val valid = generated.toString
        val uuid = UUID.fromString(valid)
        assertTrue(uuid.contains(valid))
      }

    },
    test("UUID.fromString: should fail on invalid string") {
      val invalid = "hehe"
      val uuid = UUID.fromString(invalid)
      assertTrue(uuid.isLeft)
    },
  )

}
