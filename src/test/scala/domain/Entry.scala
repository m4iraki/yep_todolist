package io.m4iraki
package domain

import zio.*
import zio.test.*
import zio.test.Assertion.*

import scala.util.Try
import cats.syntax.semigroup.*

object EntrySpec extends ZIOSpecDefault {
  import Entry.*

  def spec = suite("Entry")(
    test("Status ops: pretty") {
      assertTrue(Created.pretty == createdPretty) &&
      assertTrue(Canceled.pretty == canceledPretty) &&
      assertTrue(Done(zeroMillis).pretty == donePretty)
    },
    test("Status ops: merge should respect order Done > Canceled > Created") {
      assertTrue(Created.merge(Created) == Created) &&
      assertTrue(Created.merge(Canceled) == Canceled) &&
      assertTrue(Created.merge(doneZero) == doneZero) &&
      assertTrue(Canceled.merge(Created) == Canceled) &&
      assertTrue(Canceled.merge(Canceled) == Canceled) &&
      assertTrue(Canceled.merge(doneZero) == doneZero) &&
      assertTrue(doneZero.merge(Created) == doneZero) &&
      assertTrue(doneZero.merge(Canceled) == doneZero) &&
      assertTrue(doneZero.merge(doneZero) == doneZero)
    },
    test("Status ops: merge on Done should result in bigger time") {
      assertTrue(doneZero.merge(doneOne) == doneOne) &&
      assertTrue(doneOne.merge(doneZero) == doneOne)
    },
    test("Status ops: isXXX corresponds to XXX status") {
      val entry = make("hehe", zeroMillis)
      val canceled = entry.cancel
      val done = entry.done(zeroMillis)
      assertTrue(entry.isActive && entry.status == Created) &&
      assertTrue(canceled.isCanceled && canceled.status == Canceled) &&
      assertTrue(done.isDone && done.status.isInstanceOf[Done])
    },
    test("Status ops: done task should be not cancellable") {
      val entry = make("hehe", zeroMillis)
      val done = entry.done(zeroMillis)
      assertTrue(!done.cancelable)
    },
    test("Status ops: task done multiple times should have done time of max") {
      val entry = make("hehe", zeroMillis)
      val done1 = entry.done(zeroMillis)
      val done2 = done1.done(oneMillis)
      val done3 = done2.done(zeroMillis)
      assertTrue(done3.status == Done(oneMillis)) &&
      assertTrue(done3.orderingTime == oneMillis)
    },
    test("Merge: commutativity") {
      val entry = make("hehe", zeroMillis)
      val done = entry.done(oneMillis)
      assertTrue(done.|+|(entry) == entry.|+|(done))
    },
    test("Merge: associativity") {
      val entry = make("hehe", zeroMillis)
      val canceled = entry.cancel
      val done = entry.done(oneMillis)
      assertTrue(entry.|+|(canceled).|+|(done) == entry.|+|(canceled.|+|(done)))
    },
    test("Merge: exception on id mismatch") {
      val entry1 = make("hehe", zeroMillis)
      val entry2 = make("hehe", zeroMillis)
      val merged = Try(entry1 |+| entry2)

      assertTrue(merged.isFailure)
    },
  )

  val createdPretty = "[ ]"
  val canceledPretty = "[X]"
  val donePretty = "[V]"
  val zeroMillis: Millis = Millis.of(0L)
  val oneMillis: Millis = Millis.of(0L)
  val doneZero = Done(zeroMillis)
  val doneOne = Done(oneMillis)
}
