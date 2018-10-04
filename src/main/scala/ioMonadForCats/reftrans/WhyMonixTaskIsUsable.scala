package ioMonadForCats.reftrans

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration._

import scala.language.postfixOps

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTransMonixTask extends App {

  println("\n-----")

  val future = Task {println("processing async lazily ..."); 5}
  val fSquared1: Task[Int] = for {
    res1 <- future
    res2 <- future
  } yield res1 * res2

  println(Await.result(fSquared1.runAsync, 1 second))

  println("-----")

  val fSquared2: Task[Int] = for {
    res1 <- Task {println("processing async lazily ..."); 5}
    res2 <- Task {println("processing async lazily ..."); 5}
  } yield res1 * res2

  println(Await.result(fSquared2.runAsync, 1 second))

  println("-----\n")
}
