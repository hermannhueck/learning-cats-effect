package doc.datatypes.io

import cats.effect.{CancelToken, IO, SyncIO, Timer}
import cats.syntax.apply._
import cats.syntax.flatMap._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

object App09cRunCancel extends App {

  println("\n-----")

  // Needed for `sleep`
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  // Delayed println
  val io: IO[Unit] = IO.sleep(10 seconds) *> IO(println("Hello!"))

  val pureResult: SyncIO[IO[Unit]] = io.runCancelable { r =>
    IO(println(s"Done: $r"))
  }

  // On evaluation, this will first execute the source, then it
  // will cancel it, because it makes perfect sense :-)
  val cancel = pureResult.toIO.flatten

  val token: SyncIO[CancelToken[IO]] = cancel.runCancelable((either: Either[Throwable, Unit]) => IO[Unit] { either match {
    case Right(value) => println(value)
    case Left(throwable) => println(throwable.toString)
  }})

  token.toIO.flatten.unsafeRunSync()

  println("-----\n")
}
