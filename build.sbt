name := "learning-cats-effect"

version := "0.1.0"

scalaVersion := "2.13.2"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",        // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked",   // warn about unchecked type parameters
  "-feature",     // warn about misused language features
  "-Wconf:any:warning-verbose"
  //"-Xlint",                 // enable handy linter warnings
  //"-Xfatal-warnings",        // turn compiler warnings into errors
)

lazy val catsEffectVersion       = "2.1.3"
lazy val kindProjectorVersion    = "0.11.0"
lazy val betterMonadicForVersion = "0.3.1"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  // https://github.com/typelevel/kind-projector
  compilerPlugin(
    compilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)
  ),
  // https://github.com/oleg-py/better-monadic-for
  compilerPlugin(
    compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion)
  )
)
