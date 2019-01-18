package doc.datatypes.io

import java.io._

import cats.effect.{ContextShift, IO}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext

object App11cReadFileConcurrentClose extends App {

  println("\n-----")

  def readFile(file: File)(implicit cs: ContextShift[IO]): IO[String] = {

    // Opens file with an asynchronous boundary before it,
    // ensuring that processing doesn't block the "current thread"
    val acquire: IO[BufferedReader] = IO.shift *> IO(new BufferedReader(new FileReader(file)))

    acquire.bracket { in =>
      // Usage (the try block)
      IO {
        // Ugly, low-level Java code warning!
        val content = new StringBuilder()
        var line: String = null
        do {
          line = in.readLine()
          if (line != null) content.append(line).append('\n')
        } while (line != null)
        content.toString()
      }
    } { in =>
      // Releasing the reader (the finally block)
      // This is problematic if the resulting `IO` can get
      // canceled, because it can lead to data corruption
      IO(in.close())
    }
  }

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val ioa: IO[String] = readFile(new File("README.md"))

  val program: IO[Unit] =
    for {
      contents <- ioa
      _ <- IO { println(contents) }
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
