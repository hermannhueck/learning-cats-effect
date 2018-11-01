package doc.datatypes.io

import cats.effect.IO

import scala.language.postfixOps

object App07aIOSuspend extends App {

  println("\n-----")

/*
  // Using IO.flatMap
  def fib(n: Int, a: Long = 0, b: Long = 1): IO[Long] =
    IO(a + b).flatMap { b2 =>
      if (n > 0)
        fib(n - 1, b, b2)
      else
        IO.pure(b2)
    }
*/

  // Using IO.suspend
  def fib(n: Int, a: Long = 0, b: Long = 1): IO[Long] = {
    // println(s"n = $n , a = $a, b = $b")
    IO.suspend {
      if (n > 0)
        fib(n - 1, b, a + b)
      else
        IO.pure(a + b)
    }
  }

  val ioa = fib(5)

  ioa.map(println).unsafeRunSync()

  println("-----\n")
}
