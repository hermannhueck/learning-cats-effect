package doc.typeclasses.effect

import cats.effect.{Effect, IO, SyncIO}

object App01Effect extends App {

  println("\n-----")

  val task = IO("Hello World!")

  val ioa: SyncIO[Unit] =
    Effect[IO].runAsync(task) {
      case Right(value) => IO(println(value))
      case Left(error)  => IO.raiseError(error)
    }

  ioa.unsafeRunSync()

  println("-----\n")
}
