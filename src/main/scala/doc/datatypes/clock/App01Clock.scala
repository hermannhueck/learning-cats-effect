package doc.datatypes.clock

import cats.effect.{Clock, IO, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._

import scala.concurrent.duration.{MILLISECONDS, NANOSECONDS, TimeUnit}

import scala.language.higherKinds

object App01Clock extends App {

  println("\n-----")

  implicit val clock: Clock[IO] =
    new Clock[IO] {
      override def realTime(unit: TimeUnit): IO[Long] =
        IO(unit.convert(System.currentTimeMillis(), MILLISECONDS))

      override def monotonic(unit: TimeUnit): IO[Long] =
        IO(unit.convert(System.nanoTime(), NANOSECONDS))
    }

  def measure[F[_], A](fa: F[A])(implicit F: Sync[F], clock: Clock[F]): F[(A, Long)] =
    for {
      start  <- clock.monotonic(MILLISECONDS)
      result <- fa
      finish <- clock.monotonic(MILLISECONDS)
    } yield (result, finish - start)

  val (result, millis) = measure(IO { Thread.sleep(2000L); 5 * 5 }).unsafeRunSync()
  println(s"result = $result, millis = $millis")

  println("-----\n")
}
