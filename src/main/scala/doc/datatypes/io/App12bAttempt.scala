package doc.datatypes.io

import cats.effect.IO

object App12bAttempt extends App {

  println("\n-----")

  val boom: IO[Nothing] = IO.raiseError(new Exception("boom"))
  // boom: cats.effect.IO[Nothing] = IO(throw java.lang.Exception: boom)

  val res: Either[Throwable, Nothing] = boom.attempt.unsafeRunSync()
  // res: Either[Throwable,Nothing] = Left(java.lang.Exception: boom)

  println(res)

  println("-----\n")
}
