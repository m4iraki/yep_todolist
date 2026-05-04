package io.m4iraki
package app

import zio.*
import zio.test.*
import zio.test.Assertion.*

object All extends ZIOSpecDefault {
  def spec: Spec[TestEnvironment & Scope, Any] =
    domain.All.spec + persistence.All.spec
}
