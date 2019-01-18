package doc.typeclasses.sync

import cats.effect.{IO, Sync}

object SyncApp extends App {

  println("\n-----")

  val ioa: IO[Unit] = Sync[IO].delay(println("Hello world!"))

  ioa.unsafeRunSync()

  println("-----\n")
}
