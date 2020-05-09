package doc.typeclasses.sync

import cats.effect.{IO, Sync}

object App01Sync extends hutil.App {

  val ioa: IO[Unit] = Sync[IO].delay(println("Hello world!"))

  ioa.unsafeRunSync()
}
