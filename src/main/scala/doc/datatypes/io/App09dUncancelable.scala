package doc.datatypes.io

import cats.effect.{IO, SyncIO, Timer}
import cats.syntax.apply._
import cats.syntax.flatMap._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

object App09dUncancelable extends App {

  println("\n-----")

  // Needed for `sleep`
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  // Delayed println
  val io: IO[Unit] = IO.sleep(10 seconds) *> IO(println("Hello!"))

  // This IO can't be canceled, even if we try
  val io2 = io.uncancelable

  val pureResult: SyncIO[IO[Unit]] = io2.runCancelable { r =>
    IO(println(s"Done: $r"))
  }
  // pureResult.unsafeRunSync()     //=> Done: Right(())

  // On evaluation, this will first execute the source, then it
  // will cancel it, because it makes perfect sense :-)
  val cancel: IO[Unit] = pureResult.toIO.flatten
  cancel.unsafeRunSync() // dosn't cancel the task because it is uncancelable

  println("-----\n")
}
