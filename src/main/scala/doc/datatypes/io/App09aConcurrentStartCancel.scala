package doc.datatypes.io

import cats.effect.{ContextShift, Fiber, IO}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext

object App09aConcurrentStartCancel extends App {

  println("\n-----")

  // Needed for IO.start to do a logical thread fork
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val launchMissiles: IO[Unit] = IO.raiseError[Unit](new Exception("boom!"))
  val runToBunker: IO[Unit]    = IO(println("To the bunker!!!"))

  val program: IO[Unit] = for {
    fiber /* Fiber[IO, Unit] */ <- launchMissiles.start
    _ <- runToBunker.handleErrorWith[Unit] { error =>
          // Retreat failed, cancel launch (maybe we should
          // have retreated to our bunker before the launch?)
          println("Retreat failed, cancel launch (maybe we should have retreated to our bunker before the launch?)")
          fiber.cancel *> IO.raiseError(error)
        }
    aftermath <- fiber.join
  } yield aftermath

  println("unsafeRunAsync:")
  program.unsafeRunAsync(_ => ())

  println("\nunsafeRunCancelable:")
  program.unsafeRunCancelable(_ => ())

  println("\nunsafeRunSync:")
  program.unsafeRunSync()

  println("-----\n")
}
