package doc.datatypes.syncio

import cats.effect.{IO, SyncIO}

object App03SyncIO extends App {

  println("\n-----")

  val ioa: SyncIO[Unit] = SyncIO(println("Hello world!"))
  // ioa: cats.effect.SyncIO[Unit] = SyncIO$271806747

  val iob: IO[Unit] = ioa.to[IO]
  // iob: cats.effect.IO[Unit] = IO$741363930

  iob.unsafeRunAsync(_ => ())
  // Hello world!
  
  println("-----\n")
}
