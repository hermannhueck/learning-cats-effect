name := "learning-cats-effect"

version := "0.1.0"

scalaVersion := "2.13.1"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",        // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked",   // warn about unchecked type parameters
  "-feature"      // warn about misused language features
  //"-Ypartial-unification",  // allow the compiler to unify type constructors of different arities
  //"-Xlint",                 // enable handy linter warnings
  //"-Xfatal-warnings",        // turn compiler warnings into errors
)

lazy val catsEffectVersion       = "2.1.3"
lazy val silencerVersion         = "1.6.0"
lazy val kindProjectorVersion    = "0.11.0"
lazy val betterMonadicForVersion = "0.3.1"

libraryDependencies ++= Seq(
  "org.typelevel"   %% "cats-effect" % catsEffectVersion,
  "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full,
  // https://github.com/ghik/silencer
  "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full,
  compilerPlugin(
    "com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full
  ),
  // https://github.com/typelevel/kind-projector
  compilerPlugin(
    compilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)
  ),
  // https://github.com/oleg-py/better-monadic-for
  compilerPlugin(
    compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion)
  )
)
