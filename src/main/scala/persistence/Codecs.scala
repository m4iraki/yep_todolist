package io.m4iraki
package persistence

import domain.*

import zio.json.*

object Codecs {
  given JsonCodec[Millis] = JsonCodec[Long].transform(Millis.of, _.long)

  given JsonCodec[UUID] =
    JsonCodec[String].transformOrFail(UUID.fromString, _.asString)

  given JsonCodec[Entry.Status] = DeriveJsonCodec.gen[Entry.Status]
  given JsonCodec[Entry] = DeriveJsonCodec.gen[Entry]

  given JsonCodec[TODOList] =
    JsonCodec[Seq[Entry]].transform(TODOList.fromEntries, _.all)

}
