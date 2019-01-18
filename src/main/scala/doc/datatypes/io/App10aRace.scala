package doc.datatypes.io

import java.util.concurrent.{Executors, ScheduledExecutorService}

import cats.effect.{ContextShift, IO, Timer}

import scala.concurrent.duration._
import scala.concurrent.{CancellationException, ExecutionContext}
import scala.language.postfixOps

object App10bRaceTimer extends App {

  println("\n-----")

  def timeoutTo[A](fa: IO[A], after: FiniteDuration, fallback: IO[A])
                  (implicit timer: Timer[IO], cs: ContextShift[IO]): IO[A] = {

    IO.race(fa, timer.sleep(after)).flatMap {
      case Left(a) => IO.pure(a)
      case Right(_) => fallback
    }
  }

  def timeout[A](fa: IO[A], after: FiniteDuration)
                (implicit timer: Timer[IO], cs: ContextShift[IO]): IO[A] = {

    val error = new CancellationException(after.toString)
    timeoutTo(fa, after, IO.raiseError(error))
  }

  val ioa = IO {
    Thread.sleep(3000L)
    println("hey!")
  }

  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  val ec: ExecutionContext = ExecutionContext.fromExecutorService(scheduler)
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec, scheduler)

  val iob: IO[Unit] = timeout(ioa, 5 seconds)

  iob.unsafeRunSync()

  scheduler.shutdown()

  println("-----\n")
}
