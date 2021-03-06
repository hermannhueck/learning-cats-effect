package doc.typeclasses.async

import cats.effect.{Async, IO}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object App02Async extends hutil.App {

  val apiCall = Future.successful("I come from the Future!")

  val ioa: IO[String] = Async[IO] async { cb => apiCall onComplete { tryy => cb(tryy.toEither) } }

  ioa.map(println).unsafeRunSync()
}
