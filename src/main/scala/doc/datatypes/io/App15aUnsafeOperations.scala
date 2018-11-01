package doc.datatypes.io

import cats.effect.IO

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object App15aUnsafeOperations extends App {

  println("\n-----")

  println("\nunsafeRunSync:")
  IO(println("Sync!")).unsafeRunSync()
  // Sync!

  println("\nunsafeRunAsync:")
  IO(println("Async!")).unsafeRunAsync(_ => ())
  // Async!

  println("\nunsafeRunCancelable:")
  IO(println("Potentially cancelable!")).unsafeRunCancelable(_ => ())
  // Potentially cancelable!

  println("\nunsafeRunTimed:")
  IO(println("Timed!")).unsafeRunTimed(5.seconds)
  // Timed

  println("\nunsafeToFuture:")
  val future: Future[String] = IO("Gimme a Future!").unsafeToFuture()
  val result = Await.result(future, 3.seconds)
  println(result)
  // Gimme a Future!

  println("\n-----\n")
}
