package doc.datatypes.io

import cats.effect.IO

object App05fMyAsyncEffects extends App {

  println("\n-----")

  implicit class RichInt(num: Int) {
    def isEven: Boolean = num % 2 == 0
  }

  type Result[A] = Either[Throwable, A]
  type Callback[A] = Result[A] => Unit

  def checkEven(num: Int): Result[Int] = {

    print(s"   Computing in thread: ${Thread.currentThread().getName} ...  ")
    Thread.sleep(3000L)

    val result = if (num.isEven) Right(num) else Left(new IllegalArgumentException(s"$num is not even"))

    println(s"done: result = $result")
    result
  }

  def computeAsync[A](computation: => Result[A]): IO[A] = IO.async { cb: Callback[A] =>
    cb(computation)
  }

  val ioa: IO[Int] = computeAsync { checkEven(10) }

  val callback: Callback[Int] = result => println(result.fold(ex => ex.toString, num => s"$num is even"))

  println(s"Main: ${Thread.currentThread().getName}")
  println("Running async:")
  ioa.unsafeRunAsync(callback)

  println("\nRunning sync ...")
  // ioa.unsafeRunSync()
  ioa.map(println).unsafeRunSync()

  println("-----\n")
}
