import sbt._

object Dependencies {

  lazy val collectionCompatVersion = "2.1.6"
  lazy val shapelessVersion        = "2.3.3"
  lazy val fs2Version              = "2.3.0"
  lazy val monixVersion            = "3.2.1"
  lazy val munitVersion            = "0.7.5"
  lazy val scalaCheckVersion       = "1.14.3"

  lazy val collectionCompat   = "org.scala-lang.modules" %% "scala-collection-compat" % collectionCompatVersion
  lazy val shapeless          = "com.chuusai"            %% "shapeless"               % shapelessVersion
  lazy val fs2Core            = "co.fs2"                 %% "fs2-core"                % fs2Version
  lazy val fs2Io              = "co.fs2"                 %% "fs2-io"                  % fs2Version
  lazy val fs2ReactiveStreams = "co.fs2"                 %% "fs2-reactive-streams"    % fs2Version
  lazy val monixEval          = "io.monix"               %% "monix-eval"              % monixVersion
  lazy val munit              = "org.scalameta"          %% "munit"                   % munitVersion
  lazy val scalaCheck         = "org.scalacheck"         %% "scalacheck"              % scalaCheckVersion

  lazy val kindProjectorVersion    = "0.11.0"
  lazy val betterMonadicForVersion = "0.3.1"

  // https://github.com/typelevel/kind-projector
  lazy val kindProjectorPlugin = compilerPlugin(
    compilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)
  )
  // https://github.com/oleg-py/better-monadic-for
  lazy val betterMonadicForPlugin = compilerPlugin(
    compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion)
  )
}
