package doc.datatypes.ioapp

import cats.effect._
import cats.syntax.apply._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App01bMain extends App {

  // Needed for `IO.sleep`
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  def program(args: List[String]): IO[Unit] =
    IO.sleep(1.second) *> IO(println("Hello world!"))

  println("\n-----")

  program(args.toList).unsafeRunSync

  println("-----\n")
}