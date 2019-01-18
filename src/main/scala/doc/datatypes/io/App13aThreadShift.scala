package doc.datatypes.io

import java.util.concurrent.Executors

import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext

object App13aThreadShift extends App {

  println("\n-----")

  implicit lazy val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val task = IO(println("task"))


  // We can introduce an asynchronous boundary in the flatMap chain before a certain task:
  IO.shift(contextShift).flatMap(_ => task)
    .unsafeRunSync()


  // Note that the ContextShift value is taken implicitly from the context so you can just do this:
  IO.shift.flatMap(_ => task)
    .unsafeRunSync()

  // Or using Cats syntax:
  import cats.syntax.apply._

  (IO.shift *> task)
    .unsafeRunSync()
  // equivalent to
  (implicitly[ContextShift[IO]].shift *> task)
    .unsafeRunSync()


  // Or we can specify an asynchronous boundary “after” the evaluation of a certain task:
  task.flatMap(a => IO.shift.map(_ => a))
    .unsafeRunSync()

  // Or using Cats syntax:
  (task <* IO.shift).unsafeRunSync()
  // equivalent to
  (task <* implicitly[ContextShift[IO]].shift)
    .unsafeRunSync()

  println("-----\n")
}
