package io.m4iraki
package persistence

import domain.*
import Codecs.given

import zio.*
import zio.json.*
import zio.test.*
import zio.test.Assertion.*

object CodecsSpec extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Codecs")(
      test("Millis") {
        checkN(15)(Gen.long(0L, 1_000_000_000_000_000L)) {
          long => assertJson(Millis.of(long))
        }
      },
      test("UUID") {
        assertJson(UUID.make)
      },
      test("Entry.Status") {
        val statuses = List(
          Entry.Created,
          Entry.Canceled,
          Entry.Done(Millis.of(0L)),
        )
        statuses.foldLeft(assertTrue(true))(_ && assertJson(_))
      },
      test("Entry") {
        checkN(15)(
          Gen.alphaNumericString,
          Gen.long(0L, 1_000_000_000_000_000L),
        ) {
          case (str, long) =>
            assertJson(Entry.make(str, Millis.of(long)))
        }
      },
      test("TODOList") {
        check(
          Gen.listOfBounded(10, 20)(
            Gen.alphaNumericString zip Gen.long(0L, 1_000_000_000_000_000L),
          ),
        ) {
          inputs =>
            val entries = inputs.map {
              case (str, long) =>
                Entry.make(str, Millis.of(long))
            }
            val list = TODOList.fromEntries(entries)
            assertJson(list)
        }
      },
    )

  def assertJson[A: JsonCodec](value: A): TestResult =
    assertTrue(value.toJson.fromJson[A].contains(value))

}
