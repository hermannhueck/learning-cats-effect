package doc.datatypes.io

import cats.effect.IO

@scala.annotation.nowarn("cat=w-flag-dead-code&msg=dead code following this construct:ws")
object App12aRaiseError extends hutil.App {

  val boom: IO[Nothing] = IO.raiseError(new Exception("boom"))
  // boom: cats.effect.IO[Nothing] = IO(throw java.lang.Exception: boom)

  boom.unsafeRunSync()
  // java.lang.Exception: boom
  //   ... 43 elided
}
