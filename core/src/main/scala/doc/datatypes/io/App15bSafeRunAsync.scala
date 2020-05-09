package doc.datatypes.io

import cats.effect.{ContextShift, IO, SyncIO}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext

object App15bSafeRunAsync extends hutil.App {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  println("runAsync:")

  // Sample
  val source: IO[Int] = IO.shift *> IO(1)
  // Describes execution
  val start: SyncIO[Unit] = source.runAsync {
    case Left(e)  => IO(e.printStackTrace())
    case Right(i) => IO(println(i))
  }
  // Safe, because it does not block for the source to finish
  start.unsafeRunSync
  // 1

  Thread sleep 1000L
}
