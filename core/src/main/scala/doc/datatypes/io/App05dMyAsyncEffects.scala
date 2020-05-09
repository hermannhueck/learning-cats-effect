package doc.datatypes.io

import cats.effect.IO

object App05dMyAsyncEffects extends hutil.App {

  implicit class RichInt(num: Int) {
    def isEven: Boolean = num % 2 == 0
  }

  def checkEven(num: Int): Either[Throwable, Int] = {

    print(s"   Computing in thread: ${Thread.currentThread().getName} ...  ")
    Thread.sleep(3000L)

    val result =
      if (num.isEven)
        Right(num)
      else
        Left(new IllegalArgumentException(s"$num is not even"))

    println(s"done: result = $result")
    result
  }

  val ioa: IO[Int] = IO.async { (cb: Either[Throwable, Int] => Unit) => cb(checkEven(10)) }

  val callback: Either[Throwable, Int] => Unit = {
    case Left(throwable: Throwable) => println(throwable.toString)
    case Right(num)                 => println(s"$num is even")
  }

  println(s"Main: ${Thread.currentThread().getName}")
  println("Running async ...")
  ioa.unsafeRunAsync(callback)

  println("\nRunning sync ...")
  ioa.map(println).unsafeRunSync()
}
