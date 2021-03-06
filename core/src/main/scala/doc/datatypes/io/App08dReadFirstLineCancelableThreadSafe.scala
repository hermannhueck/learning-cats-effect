package doc.datatypes.io

import java.io.{BufferedReader, FileInputStream, InputStreamReader}
import java.util.concurrent.atomic.AtomicBoolean

import cats.effect.IO

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object App08dReadFirstLineCancelableThreadSafe extends hutil.App {

  // thread-safe impl
  def readFirstLine(in: BufferedReader)(implicit ec: ExecutionContext): IO[String] =
    IO.cancelable[String] { cb =>
      val isActive = new AtomicBoolean(true)

      ec.execute { () =>
        if (isActive.getAndSet(false)) {
          try cb(Right(in.readLine()))
          catch {
            case NonFatal(e) => cb(Left(e))
          }
        }
      // Note there's no else; if cancellation was executed
      // then we don't call the callback; task becoming
      // non-terminating ;-)
      }

      // Cancellation logic
      IO {
        // Thread-safe gate
        if (isActive.getAndSet(false))
          in.close()
      }
    }

  implicit val ec: ExecutionContext = ExecutionContext.global

  val input = new BufferedReader(new InputStreamReader(new FileInputStream("README.md"), "utf-8"))

  val ioa: IO[String] = readFirstLine(input)

  val program: IO[Unit] =
    for {
      contents <- ioa
      _        <- IO { println(contents) }
    } yield ()

  program.unsafeRunSync()
}
