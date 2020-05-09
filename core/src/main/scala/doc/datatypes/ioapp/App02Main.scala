package doc.datatypes.ioapp

import cats.effect._
import cats.syntax.functor._

object App02Main extends hutil.IOApp {

  def ioRun(args: List[String]): IO[ExitCode] =
    args.headOption match {
      case Some(name) =>
        IO(println(s"\nHello, $name.\n")).as(ExitCode.Success)
      case None =>
        IO(System.err.println("\nUsage: MyApp name\n")).as(ExitCode(2))
    }
}
