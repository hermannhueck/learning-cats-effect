package doc.datatypes.io

import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

object App05bAsyncEffects extends hutil.App {

  type Callback[A] = Either[Throwable, A] => Unit

  def convert[A](future: => Future[A]): IO[A] =
    IO.async { callback => handleIOResult(future)(callback) }

  def handleIOResult[A](future: => Future[A])(callback: Callback[A]): Unit = {
    // This triggers evaluation of the by-name param and of onComplete,
    // so it's OK to have side effects in this callback
    future.onComplete { tryy => callback(tryy.toEither) }
  }

  implicit val ec: ExecutionContext = ExecutionContext.global

  val future: Future[Int] = Future { 5 }

  val ioa: IO[Int] = convert(future)

  @scala.annotation.nowarn("cat=unused-params&msg=never used:ws")
  val program: IO[Int] =
    for {
      x <- ioa
      y <- ioa
    } yield x * x

  program.map(println).unsafeRunSync()
}
