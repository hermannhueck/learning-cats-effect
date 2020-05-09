package ioMonadForCats.rt

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

/*
  see blogpost:
  https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/cyoclj7/
 */
object WhyFutureMapIsNotRT extends hutil.App {

  implicit val ec: ExecutionContext = new ExecutionContext {
    var count = 1
    override def execute(runnable: Runnable): Unit = {
      if (count > 0) runnable.run()
      count = count - 1
    }

    override def reportFailure(cause: Throwable): Unit = ()
  }
  val f: Int => Int = _ + 1
  val g             = f

  println(Await.result(Future.successful(1).map(f andThen g), 1 second))

  println(Await.result(Future.successful(1).map(f).map(g), 1 second))
  // throws TimoutException; the 2nd invocation of map doesn't call runnable.run()
}
