package io.m4iraki
package domain

import cats.syntax.semigroup.*

opaque type TODOList = Map[UUID, Entry]

object TODOList {
  def make: TODOList = Map.empty

  def fromEntries(entries: Seq[Entry]): TODOList =
    entries.foldLeft(make){
      case (acc, entry) =>
        acc.updatedWith(entry.id){
          case None => Some(entry)
          case Some(existing) => Some(existing |+| entry)
        }
    }

  extension (list: TODOList) {

    def add(at: Millis, content: String): (UUID, TODOList) = {
      val entry = Entry.make(content, at)
      entry.id -> list.updated(entry.id, entry)
    }

    def get(taskId: UUID): Option[Entry] = list.get(taskId)

    def done(at: Millis, taskId: UUID): (Boolean, TODOList) =
      list.get(taskId) match {
        case Some(value) =>
          true -> list.updated(taskId, value.done(at))
        case None =>
          false -> list
      }

    def cancel(taskId: UUID): (Boolean, TODOList) =
      list.get(taskId) match {
        case Some(value) if value.cancelable =>
          true -> list.updated(taskId, value.cancel)
        case _ =>
          false -> list
      }

    def prune(until: Millis): TODOList =
      list.filter {
        case (id, entry) =>
          !entry.prunable || entry.orderingTime > until
      }

    private def listBy(f: Entry => Boolean): Seq[Entry] =
      list.values.filter(f).toSeq

    def listActive: Seq[Entry] = listBy(_.isActive)
    def activeCount: Int = listActive.size
    def listDone: Seq[Entry] = listBy(_.isDone)
    def doneCount: Int = listDone.size
    def listCanceled: Seq[Entry] = listBy(_.isCanceled)
    def canceledCount: Int = listCanceled.size
    def isEmpty: Boolean = listActive.isEmpty && listDone.isEmpty
    def isDone: Boolean = listActive.isEmpty
    def size: Int = activeCount + doneCount
    def all: Seq[Entry] = list.values.toSeq
    def ids: Set[UUID] = list.keySet

    def pretty: String = {
      val padding = 30
      val active = listActive
      val done = listDone
      val activeOutput =
        if active.isEmpty
        then "=== NO ACTIVE TASKS ===\n"
        else
          active
            .map(_.pretty(padding))
            .mkString(
              "===  ACTIVE  TASKS  ===\n",
              "\n",
              "\n",
            )
      val doneOutput =
        done
          .map(_.pretty(padding))
          .mkString(
            "=== COMPLETED TASKS ===\n",
            "\n",
            "",
          )
      activeOutput + doneOutput
    }

    def merge(that: TODOList): TODOList =
      list |+| that

  }

}
