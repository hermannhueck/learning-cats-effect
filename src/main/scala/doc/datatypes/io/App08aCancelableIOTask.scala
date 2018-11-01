package doc.datatypes.io

import java.io.{BufferedReader, File, FileInputStream, InputStreamReader}

import cats.effect.IO

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object App08aCancelableIOTask extends App {

  println("\n-----")

  def unsafeFileToString(file: File): String = {

    // Freaking Java :-)
    val in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))

    try {
      // Uninterruptible loop, not cancelable
      val sb = new StringBuilder()
      var hasNext = true
      while (hasNext) {
        hasNext = false
        val line = in.readLine()
        if (line != null) {
          hasNext = true
          sb.append(line)
          sb.append("\n")
        }
      }
      sb.toString
    } finally {
      in.close()
    }
  }

  def readFile(file: File)(implicit ec: ExecutionContext): IO[String] =

    IO.async[String] { cb =>

      ec.execute(() => {
        try {
          // Signal completion
          cb(Right(unsafeFileToString(file)))
        } catch {
          case NonFatal(e) => cb(Left(e))
        }
      })
    }

  implicit val ec: ExecutionContext = ExecutionContext.global

  val ioa: IO[String] = readFile(new File("README.md"))

  val program: IO[Unit] =
    for {
      contents <- ioa
      _ <- IO { println(contents) }
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
