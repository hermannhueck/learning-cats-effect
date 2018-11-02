package doc.concurrency.ref

import cats.effect.{ContextShift, IO, Sync}
import cats.effect.concurrent.Ref
import cats.implicits._

import scala.concurrent.ExecutionContext

import scala.language.higherKinds

/*
  abstract class Ref[F[_], A] {
    def get: F[A]
    def set(a: A): F[Unit]
    def modify[B](f: A => (A, B)): F[B]
    // ... and more
  }
  Ref is ...
  an asynchronous, concurrent mutable reference.
 */
object ConcurrentCounter extends App {

  println("\n-----")

  // Needed for triggering evaluation in parallel
  implicit val ctx: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  class Worker[F[_]](number: Int, ref: Ref[F, Int])(implicit F: Sync[F]) {

    private def putStrLn(value: String): F[Unit] = F.delay(println(value))

    def start: F[Unit] =
      for {
        c1 <- ref.get
        _  <- putStrLn(show"#$number >> $c1")
        c2 <- ref.modify(x => (x + 1, x))
        _  <- putStrLn(show"#$number >> $c2")
      } yield ()

  }

  val program: IO[Unit] =
    for {
      ref <- Ref.of[IO, Int](0)
      w1  = new Worker[IO](1, ref)
      w2  = new Worker[IO](2, ref)
      w3  = new Worker[IO](3, ref)
      _   <- List(
        w1.start,
        w2.start,
        w3.start
      ).parSequence.void
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
