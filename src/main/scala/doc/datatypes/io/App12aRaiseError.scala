package doc.datatypes.io

import cats.effect.IO

object App12aRaiseError extends App {

  println("\n-----")

  val boom: IO[Nothing] = IO.raiseError(new Exception("boom"))
  // boom: cats.effect.IO[Nothing] = IO(throw java.lang.Exception: boom)

  boom.unsafeRunSync()
  // java.lang.Exception: boom
  //   ... 43 elided

  println("-----\n")
}
