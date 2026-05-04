package io.m4iraki
package persistence

import zio.*
import zio.test.*
import zio.test.Assertion.*

object All extends ZIOSpecDefault {
  def spec: Spec[TestEnvironment & Scope, Any] =
    CodecsSpec.spec
}
