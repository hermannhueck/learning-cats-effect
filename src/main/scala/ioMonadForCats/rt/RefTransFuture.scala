package ioMonadForCats.rt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import scala.language.postfixOps

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTransFuture extends App {

  println("\n-----")

  val future = Future {println("processing async eagerly ..."); 5}
  val fSquared1: Future[Int] = for {
    res1 <- future
    res2 <- future
  } yield res1 * res2

  println(Await.result(fSquared1, 1 second))

  println("-----")

  val fSquared2: Future[Int] = for {
    res1 <- Future {println("processing async eagerly ..."); 5}
    res2 <- Future {println("processing async eagerly ..."); 5}
  } yield res1 * res2

  println(Await.result(fSquared2, 1 second))

  println("-----\n")
}
