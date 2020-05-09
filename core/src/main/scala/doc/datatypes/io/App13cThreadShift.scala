package doc.datatypes.io

import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext

object App13cThreadShift extends hutil.App {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  lazy val doStuff = IO(println("stuff"))

  lazy val repeat: IO[Unit] =
    for {
      _ <- doStuff
      _ <- IO.shift
      _ <- repeat
    } yield ()

  repeat.unsafeRunSync()
}
