package doc.datatypes.io

import cats.effect.IO

object App03PureValues extends hutil.App {

  val io1 = IO.pure(25).flatMap(n => IO(println(s"Number is: $n")))
  io1.unsafeRunSync()

  val io2 = IO.pure(println("THIS IS WRONG!"))
}
