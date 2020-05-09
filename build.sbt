import Dependencies._
import ScalacOptions._

val projectName        = "learning-cats-effect"
val projectDescription = "Learning functional IO using cats-effect"
val projectVersion     = "0.1.0"

val scala213 = "2.13.2"

inThisBuild(
  Seq(
    version := projectVersion,
    scalaVersion := scala213,
    publish / skip := true,
    scalacOptions ++= defaultScalacOptions,
    semanticdbEnabled := true,
    semanticdbVersion := "4.3.10", // scalafixSemanticdb.revision,
    scalafixDependencies ++= Seq("com.github.liancheng" %% "organize-imports" % "0.3.0"),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect"  % scalaVersion.value,
      shapeless,
      fs2Core,
      munit,
      kindProjectorPlugin,
      betterMonadicForPlugin
    ) ++ Seq(
      scalaCheck
    ).map(_ % Test),
    Test / parallelExecution := false,
    // run 100 tests for each property // -s = -minSuccessfulTests
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-s", "100"),
    testFrameworks += new TestFramework("munit.Framework"),
    initialCommands :=
      s"""|
          |import scala.util.chaining._
          |import fs2._, cats.effect._, cats.effect.implicits._, cats.implicits._
          |import scala.concurrent.ExecutionContext.Implicits.global
          |import scala.concurrent.duration._
          |implicit val contextShiftIO: ContextShift[IO] = IO.contextShift(global)
          |implicit val timerIO: Timer[IO] = IO.timer(global)
          |println
          |""".stripMargin // initialize REPL
  )
)

lazy val root = (project in file("."))
  .aggregate(core)
  .settings(
    name := "root",
    description := "root project",
    Compile / console / scalacOptions --= scalcOptionsToRemoveForConsole
  )

lazy val core = (project in file("core"))
  .dependsOn(hutil)
  .settings(
    name := projectName,
    description := projectDescription,
    Compile / console / scalacOptions --= scalcOptionsToRemoveForConsole,
    libraryDependencies ++= Seq(
      fs2Io,
      fs2ReactiveStreams,
      monixEval
    )
  )

lazy val hutil = (project in file("hutil"))
  .settings(
    name := "hutil",
    description := "Hermann's Utilities",
    Compile / console / scalacOptions --= scalcOptionsToRemoveForConsole
  )

// GraphBuddy support
// resolvers += Resolver.bintrayRepo("virtuslab", "graphbuddy")
// addCompilerPlugin("com.virtuslab.semanticgraphs" % "scalac-plugin" % "0.0.10" cross CrossVersion.full)
// scalacOptions += "-Yrangepos"
