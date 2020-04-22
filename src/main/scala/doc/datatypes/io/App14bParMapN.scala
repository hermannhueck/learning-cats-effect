package doc.datatypes.io

import java.util.concurrent.{Executors, ScheduledExecutorService}

import cats.effect.ExitCase.Canceled
import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.apply._
import cats.syntax.parallel._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App14bParMapN extends App {

  println("\n-----")

  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  val ec: ExecutionContext                = ExecutionContext.fromExecutorService(scheduler)
  implicit val cs: ContextShift[IO]       = IO.contextShift(ec)
  implicit val timer: Timer[IO]           = IO.timer(ec, scheduler)

  val a = IO.raiseError[Unit](new Exception("boom")) <* IO(println("Running ioA"))
  val b = (IO.sleep(1.second) *> IO(println("Running ioB")))
    .guaranteeCase {
      case Canceled => IO(println("ioB was canceled!"))
      case _        => IO.unit
    }

  val parFailure = (a, b).parMapN { (_, _) => () }

  try parFailure.unsafeRunSync()
  finally scheduler.shutdown()

  //=> ioB was canceled!
  //=> java.lang.Exception: boom
  //=> ... stack trace elided ...

  println("-----\n")
}
