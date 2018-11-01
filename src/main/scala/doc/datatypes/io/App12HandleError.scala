package doc.datatypes.io

import java.util.concurrent.{Executors, ScheduledExecutorService}

import cats.effect.{Clock, ContextShift, IO, Timer}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import scala.language.postfixOps

object App12HandleError extends App {

  println("\n-----")

  def retryWithBackoff[A](ioa: IO[A], initialDelay: FiniteDuration, maxRetries: Int)
                         (implicit timer: Timer[IO]): IO[A] = {

    ioa.handleErrorWith { error =>
      if (maxRetries > 0)
        IO.sleep(initialDelay) *> retryWithBackoff(ioa, initialDelay * 2, maxRetries - 1)
      else
        IO.raiseError(error)
    }
  }

  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  val ec: ExecutionContext = ExecutionContext.fromExecutorService(scheduler)
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = new MyTimer(ec, scheduler)

  val ioa = IO {
    println("Retrying ...!")
    throw new RuntimeException("boom")
  }

  try {
    retryWithBackoff(ioa, 1 second, 4).unsafeRunSync()
  } finally {
    scheduler.shutdown()
  }

  println("-----\n")
}
