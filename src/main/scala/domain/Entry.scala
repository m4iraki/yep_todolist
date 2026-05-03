package io.m4iraki
package domain

final case class Entry(
  id: UUID,
  content: String,
  createdAt: Millis,
  doneAt: Option[Millis],
)

object Entry {

  given Ordering[Entry] with {

    def compare(x: Entry, y: Entry): Int = {
      val comp = Ordering[Millis].compare(x.createdAt, y.createdAt)
      if comp.abs == 0
      then Ordering[Option[Millis]].compare(x.doneAt, y.doneAt)
      else comp
    }

  }

  def make(content: String, at: Millis): Entry =
    new Entry(UUID.make, content, at, None)

  extension (entry: Entry) {
    def done(at: Millis): Entry = entry.copy(doneAt = Some(at))
    def pretty(padTo: Int): String = {
      val timestamp = entry.doneAt.getOrElse(entry.createdAt).pretty
      val status = if entry.doneAt.nonEmpty then "[x]" else "[ ]"
      s"$status ${entry.content.padTo(padTo, ' ')} | $timestamp"
    }
  }

}
