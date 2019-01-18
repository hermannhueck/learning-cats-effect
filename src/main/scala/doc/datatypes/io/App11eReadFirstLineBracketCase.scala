package doc.datatypes.io

import java.io._

import cats.effect.ExitCase.{Canceled, Completed, Error}
import cats.effect.IO

object App11eReadFirstLineBracketCase extends App {

  println("\n-----")

  def readFirstLine(file: File): IO[String] = {

    val acquire = IO(new BufferedReader(new FileReader(file)))

    acquire.bracketCase { in =>
      // Usage (the try block)
      IO(in.readLine())
    } {
      case (_, Completed | Error(_)) =>
        // Do nothing
        IO.unit
      case (in, Canceled) =>
        IO(in.close())
    }
  }

  val ioa: IO[String] = readFirstLine(new File("README.md"))

  val program: IO[Unit] =
    for {
      contents <- ioa
      _ <- IO {
        println(contents)
      }
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
