package doc.datatypes.io

import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object App05aAsyncEffects extends App {

  println("\n-----")

  def convert[A](fa: => Future[A])(implicit ec: ExecutionContext): IO[A] =
    IO.async { callback =>
      // This triggers evaluation of the by-name param and of onComplete,
      // so it's OK to have side effects in this callback
      fa.onComplete {
        case Success(a) => callback(Right(a))
        case Failure(e) => callback(Left(e))
      }
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
