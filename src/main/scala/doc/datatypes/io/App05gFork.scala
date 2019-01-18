package doc.datatypes.io

import java.util.concurrent.{Executor, ExecutorService, Executors}

import cats.effect.IO

import scala.util.control.NonFatal

object App05gFork extends App {

  println("\n-----")

  def fork[A](thunk: => A)(implicit es: Executor): IO[A] = {
    IO async { cb =>
      es.execute(new Runnable {
        def run(): Unit =
          try cb(Right(thunk)) catch { case NonFatal(t) => cb(Left(t)) }
      })
    }
  }

  implicit class RichInt(num: Int) {
    def isEven: Boolean = num % 2 == 0
  }

  type ErrorOr[A] = Either[Throwable, A]
  type Callback[A] = ErrorOr[A] => Unit

  def checkEven(num: Int): Int = {

    print(s"   Computing in thread: ${Thread.currentThread().getName} ...  ")
    Thread.sleep(3000L)

    if (num.isEven) {
      println(s"done: result = $num")
      num
    } else {
      println(s"done: exception = IllegalArgumentException")
      throw new IllegalArgumentException(s"$num is not even")
    }
  }

  implicit val es: ExecutorService = Executors.newCachedThreadPool()

  val ioa: IO[Int] = fork { checkEven(11) }

  val callback: Callback[Int] = result => println(result.fold(ex => ex.toString, num => s"$num is even"))

  println(s"Main: ${Thread.currentThread().getName}")
  println("Running async ...")
  ioa.unsafeRunAsync(callback)

  // println("\nRunning sync ...")
  // ioa.unsafeRunSync()
  // ioa.map(println).unsafeRunSync()

  Thread.sleep(5000L)
  es.shutdown()

  println("-----\n")
}
