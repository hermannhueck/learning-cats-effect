package doc.datatypes.io

import cats.effect._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object App15aUnsafeOperations extends hutil.App {

  println("\nunsafeRunSync:")
  IO(println("Sync!")).unsafeRunSync()
  // Sync!

  println("\nunsafeRunAsync:")
  IO(println("Async!")).unsafeRunAsync(_ => ()): Unit
  // Async!

  println("\nunsafeRunCancelable:")
  IO(println("Potentially cancelable!")).unsafeRunCancelable(_ => ()): CancelToken[IO]
  // Potentially cancelable!

  println("\nunsafeRunTimed:")
  IO(println("Timed!")).unsafeRunTimed(5.seconds)
  // Timed

  println("\nunsafeToFuture:")
  val future: Future[String] = IO("Gimme a Future!").unsafeToFuture()
  val result                 = Await.result(future, 3.seconds)
  println(result)
  // Gimme a Future!

  println("\n-----\n")
}
