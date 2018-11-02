package doc.datatypes.contextshift

import cats.effect.{ContextShift, IO, Sync}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext

import scala.language.higherKinds

object App01ContextShift extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def fib[F[_]](n: Int, a: Long = 0, b: Long = 1)(implicit F: Sync[F], cs: ContextShift[F]): F[Long] = {
    // println(s"n = $n , a = $a, b = $b")
    F.suspend {
      if (n == 0)
        F.pure(a + b)
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

  val ioa = fib[IO](5)

  ioa.map(println).unsafeRunSync()

  println("-----\n")
}
