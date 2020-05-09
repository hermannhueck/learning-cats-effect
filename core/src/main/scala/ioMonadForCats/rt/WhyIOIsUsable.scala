package ioMonadForCats.rt

import cats.effect.IO

/*
  see blogpost:
  https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/
 */
object WhyIOIsUsable extends hutil.App {

  val task1 = {
    val r = new scala.util.Random(0L)
    val x = IO.delay(r.nextInt)
    for {
      a <- x
      b <- x
    } yield (a, b)
  }

  // Same as task1, but I inlined `x`
  val task2 = {
    val r = new scala.util.Random(0L)
    for {
      a <- IO.delay(r.nextInt)
      b <- IO.delay(r.nextInt)
    } yield (a, b)
  }

  println(task1.unsafeRunSync()) // (-1155484576,-723955400)
  println(task2.unsafeRunSync()) // (-1155484576,-723955400)

  Thread sleep 200L
}
