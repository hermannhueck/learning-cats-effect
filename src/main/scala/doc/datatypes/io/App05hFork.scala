package doc.datatypes.io

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.IO
import cats.syntax.either._

import scala.util.Try

object App05hFork extends App {

  println("\n-----")

  def fork[A](body: => A)(implicit es: ExecutorService): IO[A] = {
    IO async { cb =>
      es.execute(() => cb(Either.fromTry(Try(body))))
    }
  }

  implicit class RichInt(num: Int) {
    def isEven: Boolean = num % 2 == 0
  }

  type Result[A] = Either[Throwable, A]
  type Callback[A] = Result[A] => Unit

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

  val ioa: IO[Int] = fork { checkEven(10) }

  val callback: Callback[Int] = result => println(result.fold(ex => ex.toString, num => s"$num is even"))

  println(s"Main: ${Thread.currentThread().getName}")
  println("Running async:")
  ioa.unsafeRunAsync(callback)

  // println("\nRunning sync ...")
  // ioa.unsafeRunSync()
  // ioa.map(println).unsafeRunSync()

  Thread.sleep(5000L)
  es.shutdown()

  println("-----\n")
}
