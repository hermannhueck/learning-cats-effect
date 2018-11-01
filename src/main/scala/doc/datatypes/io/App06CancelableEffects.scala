package doc.datatypes.io

import cats.effect.{CancelToken, IO}
import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture}

import scala.concurrent.duration._
import scala.language.postfixOps

object App06CancelableEffects extends App {

  println("\n-----")

  // impl of IO.sleep
  def delayedTick(d: FiniteDuration)(implicit sc: ScheduledExecutorService): CancelToken[IO] =     //   CancelToken[IO] ^= IO[Unit]

    IO.cancelable { callback =>

      val r: Runnable = new Runnable { def run(): Unit = callback(Right(())) }
      val f: ScheduledFuture[_] = sc.schedule(r, d.length, d.unit)

      // Returning the cancellation token needed to cancel
      // the scheduling and release resources early
      IO(f.cancel(false))
    }

  implicit val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  val ioa = delayedTick(5 seconds)

  println("Sleeping 5 seconds ...")
  ioa.unsafeRunSync()
  println("... done")

  scheduler.shutdown()

  println("-----\n")
}
