package doc.datatypes.io

import java.io.{BufferedReader, File, FileInputStream, InputStreamReader}
import java.util.concurrent.atomic.AtomicBoolean

import cats.effect.IO

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object App08cReadFirstLineCancelableNotThreadSafe extends App {

  println("\n-----")

  // bad impl, not thread-safe
  def readFirstLine(in: BufferedReader)(implicit ec: ExecutionContext): IO[String] =
    IO.cancelable[String] { cb =>
      ec.execute(() => cb(
        try Right(in.readLine())
        catch { case NonFatal(e) => Left(e) }))

      // Cancellation logic is not thread-safe!
      IO(in.close())
    }

  implicit val ec: ExecutionContext = ExecutionContext.global

  val input = new BufferedReader(new InputStreamReader(new FileInputStream("README.md"), "utf-8"))

  val ioa: IO[String] = readFirstLine(input)

  val program: IO[Unit] =
    for {
      contents <- ioa
      _ <- IO { println(contents) }
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
