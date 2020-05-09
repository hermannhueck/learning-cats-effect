package doc.datatypes.io

import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

object App05cAsyncEffects extends hutil.App {

  def convert[A](fa: => Future[A])(implicit ec: ExecutionContext): IO[A] =
    IO.async { callback => fa.onComplete { tryy => callback(tryy.toEither) } }

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
