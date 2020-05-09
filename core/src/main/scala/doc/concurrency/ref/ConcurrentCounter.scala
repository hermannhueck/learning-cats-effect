package doc.concurrency.ref

import cats.effect.{ContextShift, IO, Sync}
import cats.effect.concurrent.Ref
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.parallel._
import cats.instances.list._
import cats.instances.int._
import cats.syntax.show._

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
object ConcurrentCounter extends hutil.App {

  // Needed for triggering evaluation in parallel
  implicit val ctx: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  class Worker[F[_]: Sync](number: Int, ref: Ref[F, Int]) {

    private def putStrLn(value: String): F[Unit] = Sync[F].delay(println(value))

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
      _   <- List(w1.start, w2.start, w3.start).parSequence.void
    } yield ()

  program.unsafeRunSync()
}
