package doc.datatypes.io

import cats.effect.IO

object App02StackSafety extends hutil.App {

  def fib(n: Int, a: Long = 0, b: Long = 1): IO[Long] =
    IO(a + b).flatMap { b2 =>
      if (n > 0)
        fib(n - 1, b, b2)
      else
        IO.pure(b2)
    }

  val ioa = fib(5)

  ioa.map(println).unsafeRunSync()
}
