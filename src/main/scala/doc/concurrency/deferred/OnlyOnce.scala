package doc.concurrency.deferred

import cats.effect.{ContextShift, IO}
import cats.effect.concurrent.Deferred
import cats.implicits._

import scala.concurrent.ExecutionContext

object OnlyOnce extends App {

  println("\n-----")

  // Needed for `start` or `Concurrent[IO]` and therefore `parSequence`
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def start(d: Deferred[IO, Int]): IO[Unit] = {

    val attemptCompletion: Int => IO[Unit] = n => d.complete(n).attempt.void

    List(
      IO.race(attemptCompletion(1), attemptCompletion(2)),
      d.get.flatMap { n => IO(println(show"Result: $n")) }
    ).parSequence.void
  }

  val program: IO[Unit] =
    for {
      d <- Deferred[IO, Int]
      _ <- start(d)
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
