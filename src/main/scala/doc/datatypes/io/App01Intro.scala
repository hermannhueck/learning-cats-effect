package doc.datatypes.io

import cats.effect.{IO, Sync}

object App01Intro extends App {

  println("\n-----")

  val ioa = IO { println("hey!") }

  val program: IO[Unit] =
    for {
      _ <- ioa
      _ <- ioa
    } yield ()

  program.unsafeRunSync()
  //=> hey!
  //=> hey!

  println("-----\n")
}
