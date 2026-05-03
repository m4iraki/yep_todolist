package io.m4iraki
package domain

import zio.*
import zio.test.*
import zio.test.Assertion.*

object TODOListSpec extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment & Scope, Any] = suite("TODOList")(
    test("make: should create empty list") {
      assertTrue(list.isEmpty) &&
      assertTrue(list.isDone) &&
      assertTrue(list.activeCount == 0) &&
      assertTrue(list.doneCount == 0) &&
      assertTrue(list.canceledCount == 0)
    },
    test("add: should increment size by 1 on every call") {
      oneToTenCheck {
        case ((list, assertion), int) =>
          val (_, upd) = list.add(Millis.of(0L), int.toString)
          upd -> (assertion && assertTrue(upd.size == int))
      }
    },
    test("add, get: get should get item by id that add returned") {
      oneToTenCheck {
        case ((list, assertion), int) =>
          val (id, upd) = list.add(Millis.of(0L), int.toString)
          val ok = upd.get(id).exists(_.content == int.toString)
          upd -> (assertion && assertTrue(ok))
      }
    },
    test(
      "add, done: done should move item by id that add returned from tasks to done",
    ) {
      oneToTenCheck {
        case ((list, assertion), int) =>
          val (id, upd1) = list.add(Millis.of(0L), int.toString)
          val (movedToDone, upd2) = upd1.done(Millis.of(0L), id)
          val ok = upd2.get(id).exists(_.isDone)
          upd2 -> (assertion && assertTrue(ok) && assertTrue(movedToDone))
      }
    },
    test(
      "prune: should remove all items in prunable state" +
        " if they are done before specified date",
    ) {
      val done = (1 to 10).foldLeft(list) {
        case (list, int) =>
          val (id, upd) = list.add(Millis.of(int), int.toString)
          upd.done(Millis.of(int), id)._2
      }
      val canceled = (1 to 10).foldLeft(list) {
        case (list, int) =>
          val (id, upd) = list.add(Millis.of(int), int.toString)
          upd.cancel(id)._2
      }
      val prunedDone = done.prune(Millis.of(5))
      val prunedCanceled = canceled.prune(Millis.of(5))
      def timeCheck(
        entries: Seq[Entry],
        time: Millis,
        invert: Boolean = false,
      ): Boolean =
        if invert
        then entries.forall(_.orderingTime != time)
        else entries.exists(_.orderingTime == time)
      assertTrue(
        (6 to 10).forall {
          t => timeCheck(prunedDone.listDone, Millis.of(t))
        } &&
          (1 to 5).forall {
            t => timeCheck(prunedDone.listDone, Millis.of(t), true)
          },
      ) && assertTrue(
        (6 to 10).forall {
          t => timeCheck(prunedCanceled.listCanceled, Millis.of(t))
        } &&
          (1 to 5).forall {
            t => timeCheck(prunedCanceled.listCanceled, Millis.of(t), true)
          },
      )
    },
    test("cancel: should not cancel done tasks " +
      "and return false on such request") {
      val (id, upd1) = list.add(Millis.of(0L), "hehe")
      val (_, upd2) = upd1.done(Millis.of(0L), id)
      val (canceled, upd3) = upd2.cancel(id)
      assertTrue(!canceled) && assertTrue(upd2 == upd3)
    },
  )

  val list: TODOList = TODOList.make

  def oneToTenCheck(
    fold: ((TODOList, TestResult), Int) => (TODOList, TestResult),
  ): TestResult = (1 to 10).foldLeft(list -> assertTrue(true))(fold)._2

}
