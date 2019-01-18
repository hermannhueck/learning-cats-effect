package doc.datatypes.io

import java.util.concurrent.{Executors, ScheduledExecutorService}

import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

object App10aRace extends App {

  println("\n-----")

  def race[A, B](lh: IO[A], rh: IO[B])(implicit cs: ContextShift[IO]): IO[Either[A, B]] =
    IO.racePair(lh, rh).flatMap {
      case Left((a, fiber)) =>
        fiber.cancel.map(_ => Left(a))
      case Right((fiber, b)) =>
        fiber.cancel.map(_ => Right(b))
    }

  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  val ec: ExecutionContext = ExecutionContext.fromExecutorService(scheduler)
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec, scheduler)

  val io1 = IO.sleep(3.seconds) *> IO(println("hey!"))
  val io2 = IO.sleep(5.seconds) *> IO(println("hello!"))

  val io: IO[Either[Unit, Unit]] = race(io1, io2)

  io.map(println).unsafeRunSync()

  scheduler.shutdown()

  println("-----\n")
}
