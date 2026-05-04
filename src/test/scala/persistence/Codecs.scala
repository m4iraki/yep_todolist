package io.m4iraki
package persistence

import domain.*
import Codecs.given

import zio.*
import zio.json.*
import zio.test.*
import zio.test.Assertion.*

object CodecsSpec extends ZIOSpecDefault {
  import CommonSpec.{genUUID, genMillis}

  def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Codecs")(
      suite("Roundtrip")(
        test("Millis") {
          check(genMillis)(assertRoundtrip)
        },
        test("UUID") {
          check(genUUID)(assertRoundtrip)
        },
        test("Entry.Status") {
          val statuses = List(
            Entry.Created,
            Entry.Canceled,
            Entry.Done(Millis.of(0L)),
          )
          statuses.foldLeft(assertTrue(true))(_ && assertRoundtrip(_))
        },
        test("Entry") {
          check(
            Gen.alphaNumericString,
            genMillis,
          ) {
            case (str, millis) =>
              assertRoundtrip(Entry.make(str, millis))
          }
        },
        test("TODOList") {
          check(
            Gen.listOfBounded(10, 20)(
              Gen.alphaNumericString zip genMillis,
            ),
          ) {
            inputs =>
              val entries = inputs.map {
                case (str, millis) =>
                  Entry.make(str, millis)
              }
              val list = TODOList.fromEntries(entries)
              assertRoundtrip(list)
          }
        },
      ),
      suite("Given values")(
        test("UUID: generated should be parsed correctly") {
          check(Gen.uuid) {
            uuid =>
              val str = s"\"${uuid.toString}\""
              val id = UUID.fromString(uuid.toString)
              assertTrue(str.fromJson[UUID].exists(id.contains))
          }
        },
        test("UUID: incorrect should result in error") {
          assertTrue("\"hehe\"".fromJson[UUID].isLeft)
        },
      ),
    )

  def assertRoundtrip[A: JsonCodec](value: A): TestResult =
    assertTrue(value.toJson.fromJson[A].contains(value))

}
