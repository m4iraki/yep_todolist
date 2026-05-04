package io.m4iraki
package app

import zio.json.*
import persistence.Codecs.given
import domain.UUID
@main
def main(): Unit = {
  println("yep todo")
  val uuid = UUID.make
  println(uuid.toJson)
  println(uuid.toJson.fromJson[UUID])
}
