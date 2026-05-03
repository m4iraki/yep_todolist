package io.m4iraki
package domain

import zio.*
import zio.test.*
import zio.test.Assertion.*

object DomainSpec extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment & Scope, Any] =
    CommonSpec.spec +
      EntrySpec.spec +
      TODOListSpec.spec

}
