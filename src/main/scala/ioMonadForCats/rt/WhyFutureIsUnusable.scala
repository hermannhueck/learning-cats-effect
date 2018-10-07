package ioMonadForCats.rt

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import scala.language.postfixOps

/*
  see blogpost:
  https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/
 */
object WhyFutureIsUnusable extends App {

  println("\n-----")

  val f1 = {
    val r = new Random(0L)
    val x = Future(r.nextInt)
    for {
      a <- x
      b <- x
    } yield (a, b)
  }

  // Same as f1, but I inlined `x`
  val f2 = {
    val r = new Random(0L)
    for {
      a <- Future(r.nextInt)
      b <- Future(r.nextInt)
    } yield (a, b)
  }

  f1.onComplete(println) // Success((-1155484576,-1155484576))
  f2.onComplete(println) // Success((-1155484576,-723955400))    <-- not the same

  Await.ready(f1, 1 second)
  Await.ready(f2, 1 second)

  println("-----\n")
}
