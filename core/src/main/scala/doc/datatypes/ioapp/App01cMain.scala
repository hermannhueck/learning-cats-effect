package doc.datatypes.ioapp

import cats.effect._
import cats.syntax.functor._
import cats.syntax.apply._

import scala.concurrent.duration._

object App01cMain extends hutil.IOApp {

  def ioRun(args: List[String]): IO[ExitCode] =
    (for {
      _ <- IO.sleep(1.second) *> IO(println("Hello world!"))
    } yield ()).as(ExitCode.Success)
}
