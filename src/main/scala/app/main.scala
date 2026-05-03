package io.m4iraki
package app

import domain.*

@main
def main(): Unit = {
  val list = TODOList.make
  println(list.pretty)
  val (tasks, upd1) = List(
    "Create TODO List",
    "Make Naive Implementation",
  ).foldLeft((List.empty[UUID], TODOList.make)) {
    case ((ids, list), task) =>
      val (id, upd) = list.add(Millis.now, task)
      (id :: ids, upd)
  }
  println(upd1.pretty)
  val (_, upd2) =
    upd1.add(Millis.now, "Implement HTTP Server via http4s")
      ._2.add(Millis.now, "Add simple persistence")
  println(upd2.pretty)
  val upd3 = upd2.done(
    Millis.now,
    tasks: _*,
  )
  println(upd3.pretty)
}
