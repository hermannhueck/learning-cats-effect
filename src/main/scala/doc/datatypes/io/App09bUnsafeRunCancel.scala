package doc.datatypes.io

import cats.effect.{CancelToken, IO, Timer}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

object App09bUnsafeRunCancel extends App {

  println("\n-----")

  // Needed for `sleep`
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  // Delayed println
  val io: IO[Unit] = IO.sleep(10 seconds) *> IO(println("Hello!"))

  val cancel: IO[Unit] =
    io.unsafeRunCancelable(r => println(s"Done: $r"))

  // ... if a race condition happens, we can cancel it,
  // thus canceling the scheduling of `IO.sleep`

  cancel.unsafeRunSync()

  println("-----")

  val token: CancelToken[IO] =
    cancel.unsafeRunCancelable((either: Either[Throwable, Unit]) =>
      IO[Unit] {
        either match {
          case Right(value)    => println(value)
          case Left(throwable) => println(throwable.toString)
        }
      }
    )

  token.unsafeRunSync()

  println("-----\n")
}
