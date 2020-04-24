package doc.concurrency.semaphore

import cats.Parallel
import cats.effect.{Concurrent, ContextShift, IO, Sync, Timer}
import cats.effect.concurrent.Semaphore
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.parallel._
import cats.instances.list._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import cats.effect.IOApp
import cats.effect.ExitCode
import cats.effect.concurrent.MVar

/*
  abstract class MVar[F[_], A] {
    def put(a: A): F[Unit]
    def take: F[A]
    def read: F[A]

    def tryPut(a: A): F[Boolean]
    def tryTake: F[Option[A]]
  }
  MVar is ...
  a mutable location that can be empty or contain a value, asynchronously blocking reads when empty and blocking writes when full.
 */
object SharedResource3 extends IOApp {

  class PreciousResource[F[_]: Concurrent: Timer](name: String, s: MVar[F, Unit]) {
    def use: F[Unit] =
      for {
        x <- s.isEmpty
        _ <- Sync[F].delay(println(s"$name >> Check | Availability: ${!x} - now trying to acquire resource"))
        _ <- s.take
        y <- s.isEmpty
        _ <- Sync[F].delay(println(s"$name >>>> Acquired | Availability: ${!y}"))
        _ <- Timer[F].sleep(1.second)
        _ <- s.put(())
        z <- s.isEmpty
        _ <- Sync[F].delay(println(s"$name <<<< Released | Availability: ${!z}"))
      } yield ()
  }

  val program: IO[Unit] =
    for {
      _  <- IO(println("\n-----"))
      s  <- MVar[IO].of(())
      r1 = new PreciousResource[IO]("R1", s)
      r2 = new PreciousResource[IO]("R2", s)
      r3 = new PreciousResource[IO]("R3", s)
      _  <- List(r1.use, r2.use, r3.use).parSequence.void
      _  <- IO(println("-----\n"))
    } yield ()

  def run(args: List[String]): IO[ExitCode] =
    program.as(ExitCode.Success)
}
