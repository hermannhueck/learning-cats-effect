package doc.typeclasses.async

import cats.effect.{IO, Async}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Success, Failure}

object App01Async extends App {

  println("\n-----")

  val apiCall = Future.successful("I come from the Future!")

  val ioa: IO[String] = Async[IO].async { cb =>

      apiCall.onComplete {
        case Success(value) => cb(Right(value))
        case Failure(error) => cb(Left(error))
      }
    }

  ioa.map(println).unsafeRunSync()

  println("-----\n")
}
