package io.m4iraki
package domain

import zio.*
import zio.test.*
import zio.test.Assertion.*

object TODOListSpec extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment & Scope, Any] = suite("TODOList")(
    test("make: should create empty list") {
      val list = TODOList.make
      assertTrue(list.tasksList.isEmpty && list.doneList.isEmpty)
    },
    test("make: should create different list") {
      val list1 = TODOList.make
      val list2 = TODOList.make
      assertTrue(list1 ne list2)
    },
    test("push: should increment size by 1 on every call") {
      val list = TODOList.make
      val (_, success) = (1 to 10).foldLeft(list -> true) {
        case ((l, success), int) =>
          val upd = l.push(Millis.now, int.toString)
          upd -> (success && int == upd.tasksList.size)
      }
      assertTrue(success)
    },
    test("add: should increment size by 1 on every call") {
      val list = TODOList.make
      val (_, success) = (1 to 10).foldLeft(list -> true) {
        case ((l, success), int) =>
          val (_, upd) = l.add(Millis.now, int.toString)
          upd -> (success && int == upd.tasksList.size)
      }
      assertTrue(success)
    },
    test("add, get: get should get item by id that add returned") {
      val list = TODOList.make
      val (_, success) = (1 to 10).foldLeft(list -> true) {
        case ((l, success), int) =>
          val (id, upd) = l.add(Millis.now, int.toString)
          val ok = upd.get(id).exists(_.content == int.toString)
          upd -> (success && ok)
      }
      assertTrue(success)
    },
    test("add, done: done should move item by id that add returned from tasks to done") {
      val list = TODOList.make
      val (_, success) = (1 to 10).foldLeft(list -> true) {
        case ((l, success), int) =>
          val (id, upd1) = l.add(Millis.now, int.toString)
          val upd2 = upd1.done(Millis.now, id)
          val ok = upd2.get(id).isEmpty && upd2.getDone(id).exists(_.content == int.toString)
          upd2 -> (success && ok)
      }
      assertTrue(success)
    },
    test("prune: should remove all items in done if they are done before specified date") {
      val list = TODOList.make
      val pruneAt = Millis.of(5)
      val filled = (1 to 10).foldLeft(list) {
        case (l, int) =>
          val (id, upd) = l.add(Millis.of(int), int.toString)
          upd.done(Millis.of(int), id)
      }
      val pruned = filled.pruneDone(pruneAt)
      val prunedNotContainsPruned =
        pruned.doneList.forall(_.doneAt.exists(_ > pruneAt)) &&
          pruned.doneList.size == 5
      assertTrue(prunedNotContainsPruned)
    },
  )

}
