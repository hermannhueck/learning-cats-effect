package ioMonadForCats.rt

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Random, Try}
import scala.language.postfixOps

/*
  see blogpost:
  https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/
 */
object WhyMonixTaskIsUsable extends App {

  println("\n-----")

  val task1 = {
    val r = new Random(0L)
    val x = Task.delay(r.nextInt)
    for {
      a <- x
      b <- x
    } yield (a, b)
  }

  // Same as task1, but I inlined `x`
  val task2 = {
    val r = new Random(0L)
    for {
      a <- Task.delay(r.nextInt)
      b <- Task.delay(r.nextInt)
    } yield (a, b)
  }

  task1 runAsync println // (-1155484576,-723955400)
  task2 runAsync println // (-1155484576,-723955400)

  Await.ready(task1.runToFuture, 1 second)
  Await.ready(task2.runToFuture, 1 second)

  println("-----\n")
}
