package doc.concurrency.semaphore

import cats.Parallel
import cats.effect.{Concurrent, ContextShift, IO, Timer}
import cats.effect.concurrent.Semaphore
import cats.implicits._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import scala.language.higherKinds

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
object SharedResource extends App {

  println("\n-----")

  // Needed for getting a Concurrent[IO] instance
  implicit val ctx: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  // Needed for `sleep`
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  class PreciousResource[F[_]](name: String, s: Semaphore[F])(implicit F: Concurrent[F], timer: Timer[F]) {
    def use: F[Unit] =
      for {
        x <- s.available
        _ <- F.delay(println(s"$name >> Availability: $x"))
        _ <- s.acquire
        y <- s.available
        _ <- F.delay(println(s"$name >> Started | Availability: $y"))
        _ <- timer.sleep(3.seconds)
        _ <- s.release
        z <- s.available
        _ <- F.delay(println(s"$name >> Done | Availability: $z"))
      } yield ()
  }

  implicit val par: Parallel[IO, IO] = Parallel[IO, IO.Par].asInstanceOf[Parallel[IO, IO]]

  val program: IO[Unit] =
    for {
      s  <- Semaphore[IO](1)
      r1 = new PreciousResource[IO]("R1", s)
      r2 = new PreciousResource[IO]("R2", s)
      r3 = new PreciousResource[IO]("R3", s)
      _  <- List(r1.use, r2.use, r3.use).parSequence.void
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
