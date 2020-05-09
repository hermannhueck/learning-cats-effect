package doc.typeclasses.async

import cats.effect.{Async, IO}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object App01Async extends hutil.App {

  val apiCall = Future.successful("I come from the Future!")

  val ioa: IO[String] = Async[IO].async { cb =>
    apiCall.onComplete {
      case Success(value) => cb(Right(value))
      case Failure(error) => cb(Left(error))
    }
  }

  ioa.map(println).unsafeRunSync()
}
