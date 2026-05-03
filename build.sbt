ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"
val zioVersion = "2.1.21"

lazy val root = (project in file("."))
  .settings(
    name := "yep_todolist",
    idePackagePrefix := Some("io.m4iraki"),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
      "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
    ),

    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  )
