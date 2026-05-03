package io.m4iraki
package domain

final case class Entry(
  id: UUID,
  content: String,
  createdAt: Millis,
  status: Entry.Status,
)

object Entry {

  enum Status {
    case Created
    case Canceled
    case Done(at: Millis)
  }

  export Status.*

  extension (status: Status) {

    def pretty: String = status match {
      case Status.Created  => "[ ]"
      case Status.Canceled => "[X]"
      case Status.Done(at) => "[V]"
    }

    def merge(that: Status): Status =
      (status, that) match {
        case (Done(a1), Done(a2))   => Done(Ordering[Millis].max(a1, a2))
        case (done: Done, _)        => done
        case (Created, e)           => e
        case (Canceled, done: Done) => done
        case (Canceled, _)          => Canceled
      }

  }

  given Ordering[Entry] =
    Ordering.by[Entry, Millis](_.orderingTime)

  def make(content: String, at: Millis): Entry =
    new Entry(UUID.make, content, at, Created)

  extension (entry: Entry) {

    def orderingTime: Millis = entry.status match {
      case Done(at) => at
      case _        => entry.createdAt
    }

    def done(at: Millis): Entry =
      entry.copy(status = entry.status.merge(Done(at)))

    def isDone: Boolean = entry.status match {
      case Status.Done(at) => true
      case _               => false
    }

    def isActive: Boolean = entry.status == Created
    def isCanceled: Boolean = entry.status == Canceled

    def cancel: Entry = {
      assume(cancelable)
      entry.copy(status = Status.Canceled)
    }

    def cancelable: Boolean = entry.status match {
      case Done(_) => false
      case _       => true
    }

    def prunable: Boolean = entry.status != Created

    def pretty(padTo: Int): String = {
      val timestamp = entry.orderingTime.pretty
      val status = entry.status.pretty
      s"$status ${entry.content.padTo(padTo, ' ')} | $timestamp"
    }

    private[domain] def merge(that: Entry): Entry = {
      assume(that.id == entry.id)
      entry.copy(status = entry.status.merge(that.status))
    }

  }

}
