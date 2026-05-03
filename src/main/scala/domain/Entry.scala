package io.m4iraki
package domain

final case class Entry(
  id: UUID,
  content: String,
  createdAt: Millis,
  doneAt: Option[Millis],
)

object Entry {

  given Ordering[Entry] =
    Ordering.by[Entry, Millis](_.createdAt)
      .orElse(Ordering.by[Entry, Option[Millis]](_.doneAt))

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
