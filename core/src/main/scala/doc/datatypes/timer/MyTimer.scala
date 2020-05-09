package doc.datatypes.timer

import java.util.concurrent.ScheduledExecutorService

import cats.effect.{Clock, IO, Timer}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, NANOSECONDS, TimeUnit}

final class MyTimer(ec: ExecutionContext, sc: ScheduledExecutorService) extends Timer[IO] {

  override val clock: Clock[IO] =
    new Clock[IO] {
      override def realTime(unit: TimeUnit): IO[Long] =
        IO(unit.convert(System.currentTimeMillis(), MILLISECONDS))

      override def monotonic(unit: TimeUnit): IO[Long] =
        IO(unit.convert(System.nanoTime(), NANOSECONDS))
    }

  @scala.annotation.nowarn("cat=w-flag-value-discard&msg=discarded non-Unit value:ws")
  override def sleep(timespan: FiniteDuration): IO[Unit] =
    IO.cancelable { cb =>
      val tick = new Runnable {
        def run(): Unit =
          ec.execute(new Runnable {
            def run(): Unit = cb(Right(()))
          })
      }
      val f = sc.schedule(tick, timespan.length, timespan.unit)
      IO(f.cancel(false))
    }
}
