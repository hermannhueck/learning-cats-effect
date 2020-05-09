package doc.datatypes.ioapp

import cats.effect._
import cats.syntax.apply._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object App01aMain {

  // Needed for `IO.sleep`
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  def program(): IO[Unit] =
    IO.sleep(1.second) *> IO(println("Hello world!"))

  def main(args: Array[String]): Unit = {
    println("\n-----")
    program().unsafeRunSync
    println("-----\n")
  }
}
