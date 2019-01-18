package doc.datatypes.io

import java.io._

import cats.effect.IO

import scala.concurrent.ExecutionContext

object App11bResource extends App {

  println("\n-----")

  def readFirstLine(file: File): IO[String] =
    IO(new BufferedReader(new FileReader(file))).bracket { in =>
      // Usage (the try block)
      IO(in.readLine())
    } { in =>
      // Releasing the reader (the finally block)
      IO(in.close())
    }

  val ioa: IO[String] = readFirstLine(new File("README.md"))

  val program: IO[Unit] =
    for {
      contents <- ioa
      _ <- IO { println(contents) }
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
