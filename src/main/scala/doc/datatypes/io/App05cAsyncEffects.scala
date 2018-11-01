package doc.datatypes.io

import cats.syntax.either._
import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

object App05cAsyncEffects extends App {

  println("\n-----")

  def convert[A](fa: => Future[A])(implicit ec: ExecutionContext): IO[A] =
    IO.async { callback =>
      fa.onComplete { t =>
        callback(Either.fromTry(t)) }
    }

  implicit val ec: ExecutionContext = ExecutionContext.global

  val future: Future[Int] = Future { 5 }

  val ioa: IO[Int] = convert(future)

  val program: IO[Int] =
    for {
      x <- ioa
      y <- ioa
    } yield x * x

  program.map(println).unsafeRunSync()

  println("-----\n")
}
