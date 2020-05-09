package doc.datatypes.io

import java.io._

import cats.effect.{ContextShift, IO}
import cats.syntax.apply._

import scala.concurrent.ExecutionContext

object App11dReadFileSynchronized extends hutil.App {

  def readFile(file: File): IO[String] = {

    // Opens file with an asynchronous boundary before it,
    // ensuring that processing doesn't block the "current thread"
    val acquire = IO.shift *> IO(new BufferedReader(new FileReader(file)))

    // Suspended execution because we are going to mutate
    // a shared variable
    IO.suspend {
      // Shared state meant to signal cancellation
      var isCanceled = false

      acquire.bracket { in =>
        IO {
          val content      = new StringBuilder()
          var line: String = null
          do {
            // Synchronized access to isCanceled and to the reader
            line = in.synchronized {
              if (!isCanceled)
                in.readLine()
              else
                null
            }
            if (line != null) content.append(line).append('\n')
          } while (line != null)
          content.toString()
        }
      } { in =>
        IO {
          // Synchronized access to isCanceled and to the reader
          in.synchronized {
            isCanceled = true
            in.close()
          }
        }
      }
    }
  }

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val ioa: IO[String] = readFile(new File("README.md"))

  val program: IO[Unit] =
    for {
      contents <- ioa
      _        <- IO { println(contents) }
    } yield ()

  program.unsafeRunSync()
}
