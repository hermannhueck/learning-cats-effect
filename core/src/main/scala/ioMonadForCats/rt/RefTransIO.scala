package ioMonadForCats.rt

import cats.effect.IO

import scala.concurrent.ExecutionContext.Implicits.global

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTransIO extends hutil.App {

  val task: IO[Int] = IO { println("processing async lazily ..."); 5 }
  val taskSquared1: IO[Int] = for {
    res1 <- task
    res2 <- task
  } yield res1 * res2

  taskSquared1.unsafeToFuture foreach println

  println("-----")

  val taskSquared2: IO[Int] = for {
    res1 <- IO { println("processing async lazily ..."); 5 }
    res2 <- IO { println("processing async lazily ..."); 5 }
  } yield res1 * res2

  taskSquared2.unsafeToFuture foreach println

  Thread sleep 200L
}
