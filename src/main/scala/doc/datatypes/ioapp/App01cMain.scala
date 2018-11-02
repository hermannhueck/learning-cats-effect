package doc.datatypes.ioapp

import cats.effect._
import cats.syntax.functor._
import cats.syntax.apply._

import scala.concurrent.duration._

object App01cMain extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    (for {
      _ <- IO { println("\n-----") }
      _ <- IO.sleep(1.second) *> IO(println("Hello world!"))
      _ <- IO { println("-----\n") }
    } yield ()).as(ExitCode.Success)
}