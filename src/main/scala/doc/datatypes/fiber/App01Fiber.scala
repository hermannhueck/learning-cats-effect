package doc.datatypes.fiber

import cats.effect.{ContextShift, Fiber, IO, SyncIO}

import scala.concurrent.ExecutionContext

object App01Fiber extends App {

  println("\n-----")

  // Needed for `start`
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val io = IO(println("Hello!"))
  val fiber: IO[Fiber[IO, Unit]] = io.start

  fiber.unsafeRunSync()

  println("-----\n")
}
