package doc.datatypes.io

import cats.effect.{ContextShift, IO}

import cats.syntax.apply._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

object App07bIOSuspendWithAsyncBoundaries extends hutil.App {

  /*
  // Using IO.flatMap
  def fib(n: Int, a: Long = 0, b: Long = 1): IO[Long] =
    IO(a + b).flatMap { b2 =>
      if (n > 0)
        fib(n - 1, b, b2)
      else
        IO.pure(b2)
    }

  // Using IO.suspend
  def fib(n: Int, a: Long = 0, b: Long = 1): IO[Long] =
    IO.suspend {
      if (n > 0)
        fib(n - 1, b, a + b)
      else
        IO.pure(a + b)
    }
   */

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def fib(n: Int, a: Long = 0, b: Long = 1)(implicit cs: ContextShift[IO]): IO[Long] = {
    // println(s"n = $n , a = $a, b = $b")
    IO.suspend {
      if (n == 0)
        IO.pure(a + b)
      else {
        val next = fib(n - 1, b, a + b)
        // Every 100 cycles, introduce a logical thread fork
        if (n % 100 == 0)
          cs.shift *> next
        else
          next
      }
    }
  }

  val ioa = fib(5)

  ioa.map(println).unsafeRunSync()
}
