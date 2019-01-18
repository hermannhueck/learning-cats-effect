package doc.datatypes.io

import cats.effect.{IO, SyncIO, Timer}
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
  // pureResult.unsafeRunSync()     //=> Done: Right(())

  // On evaluation, this will first execute the source, then it
  // will cancel it, because it makes perfect sense :-)
  val cancel: IO[Unit] = pureResult.toIO.flatten
  cancel.unsafeRunSync() // cancels the task and prints nothing

  println("-----\n")
}
