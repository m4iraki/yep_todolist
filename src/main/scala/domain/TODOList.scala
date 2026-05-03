package io.m4iraki
package domain

final case class TODOList private (
                                    private val tasks: Map[UUID, Entry],
                                    doneList: List[Entry],
)

object TODOList {
  def make: TODOList = new TODOList(Map.empty, Nil)

  extension (list: TODOList) {

    def add(at: Millis, content: String): (UUID, TODOList) = {
      val entry = Entry.make(content, at)
      entry.id -> TODOList(list.tasks.updated(entry.id, entry), list.doneList)
    }

    def done(at: Millis, taskIds: UUID*): TODOList = {
      val updatedMap = list.tasks -- taskIds
      val tasks = for {
        taskId <- taskIds
        entry <- list.tasks.get(taskId)
      } yield entry.done(at)
      val updatedDone = list.doneList prependedAll tasks
      TODOList(updatedMap, updatedDone)
    }

    def pruneDone(until: Millis): TODOList =
      TODOList(list.tasks, list.doneList.filter(_.doneAt.exists(_ < until)))

    def tasksList: Seq[Entry] = list.tasks.values.toSeq.sorted

    def pretty: String = {
      val padding = 30
      val activeOutput =
        if list.tasks.isEmpty then "=== NO ACTIVE TASKS ===\n"
        else
          list.tasksList
            .map(_.pretty(padding))
            .mkString(
              "===  ACTIVE  TASKS  ===\n",
              "\n",
              "\n",
            )
      list.doneList
        .map(_.pretty(padding))
        .mkString(
          activeOutput + "=== COMPLETED TASKS ===\n",
          "\n",
          "",
        )
    }

  }

}
