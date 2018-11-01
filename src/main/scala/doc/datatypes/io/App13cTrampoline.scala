package doc.datatypes.io

import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext

object App13cTrampoline extends App {

  println("\n-----")

  def signal[A](a: A): IO[A] = IO.async(_(Right(a)))

  def loop(n: Int): IO[Int] = {
    println(s"Looping: n = $n")
    signal(n).flatMap { x =>
      if (x > 0) loop(n - 1) else IO.pure(0)
    }
  }

  loop(5).unsafeRunSync()

  println("-----\n")
}
