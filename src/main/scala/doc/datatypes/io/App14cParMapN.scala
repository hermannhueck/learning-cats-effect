package doc.datatypes.io

import java.util.concurrent.{Executors, ScheduledExecutorService}

import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.apply._
import cats.syntax.parallel._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App14cParMapN extends App {

  println("\n-----")

  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  val ec: ExecutionContext = ExecutionContext.fromExecutorService(scheduler)
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec, scheduler)

  val ioA = IO.sleep(10.seconds) *> IO(println("Delayed!"))
  val ioB = IO.raiseError[Unit](new Exception("dummy"))

  val program = (ioA, ioB).parMapN { (_, _) => () }

  try
    program.unsafeRunSync()
  finally
    scheduler.shutdown()
  //=> Running ioB
  //=> Running ioC
  //=> Running ioA

  println("-----\n")
}
