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

/*
  abstract class Ref[F[_], A] {
    def available: F[Long]
    def acquire: F[Unit]
    def release: F[Unit]
    // ... and more
  }
  A Semaphore has ...
  a non-negative number of permits available. Acquiring a permit decrements the current number of permits and releasing a permit increases the current number of permits.
  An acquire that occurs when there are no permits available results in semantic blocking until a permit becomes available.
 */
object SharedResource2 extends IOApp {

  class PreciousResource[F[_]: Concurrent: Timer](name: String, s: Semaphore[F]) {
    def use: F[Unit] =
      for {
        x <- s.available
        _ <- Sync[F].delay(println(s"$name >> Check | Availability: $x - now trying to acquire resource"))
        _ <- s.acquire
        y <- s.available
        _ <- Sync[F].delay(println(s"$name >>>> Acquired | Availability: $y"))
        _ <- Timer[F].sleep(1.second)
        _ <- s.release
        z <- s.available
        _ <- Sync[F].delay(println(s"$name <<<< Released | Availability: $z"))
      } yield ()
  }

  val program: IO[Unit] =
    for {
      _  <- IO(println("\n-----"))
      s  <- Semaphore[IO](1)
      r1 = new PreciousResource[IO]("R1", s)
      r2 = new PreciousResource[IO]("R2", s)
      r3 = new PreciousResource[IO]("R3", s)
      _  <- List(r1.use, r2.use, r3.use).parSequence.void
      _  <- IO(println("-----\n"))
    } yield ()

  def run(args: List[String]): IO[ExitCode] =
    program.as(ExitCode.Success)
}
