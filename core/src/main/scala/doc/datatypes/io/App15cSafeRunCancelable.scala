package doc.datatypes.io

import cats.effect.{CancelToken, ContextShift, IO, SyncIO}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext

object App15cSafeRunCancelable extends hutil.App {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  println("runCancelable:")

  // Sample
  val source = IO.shift *> IO(1)
  // Describes interruptible execution
  val start: SyncIO[CancelToken[IO]] = source.runCancelable {
    case Left(e)  => IO(e.printStackTrace())
    case Right(i) => IO(println(i))
  }

  // Safe, because it does not block for the source to finish
  val cancel: CancelToken[IO] = start.unsafeRunSync

  // Safe, because cancellation only sends a signal,
  // but doesn't back-pressure on anything
  cancel.unsafeRunSync
  // prints nothing
  // prints 1 if cancellation is not invoked

  Thread sleep 1000L
}
