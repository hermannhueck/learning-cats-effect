package tutorial.filecopy

import java.io._

import cats.effect._
import cats.effect.concurrent.Semaphore

import cats.syntax.functor._
import cats.syntax.flatMap._

/*
  See tutorial and solutions at:
  https://typelevel.org/cats-effect/tutorial/tutorial.html#tcp-echo-server---concurrent-system-with-fibers
  https://github.com/lrodero/cats-effect-tutorial
 */
object Copy06IOApp extends IOApp {

  // IOApp allows for purely functional code
  // provides implicit instances of Timer[IO] and ContextShift[IO]

  val usage = s"Usage: ${Copy01Resource.getClass.getSimpleName.replaceFirst(".$", "")} inputFile outputFile"

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- if (args.length < 2)
            IO.raiseError(new IllegalArgumentException(s"Need origin and destination files\n$usage"))
          else IO.unit
      _ <- IO(println("\n-----"))
      //_     <- timer.sleep(3.seconds)
      orig  = new File(args(0))
      dest  = new File(args(1))
      count <- copy(orig, dest)
      _     <- IO(println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}"))
      _     <- IO(println("-----\n"))
    } yield ExitCode.Success

  def copy(origin: File, destination: File)(implicit concurrent: Concurrent[IO]): IO[Long] =
    for {
      guard <- Semaphore[IO](1)
      count <- inputOutputStreams(origin, destination, guard).use {
                case (in, out) =>
                  guard.withPermit(transfer(in, out))
              }
    } yield count

  def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
    for {
      buffer <- IO(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
      total  <- transmit(origin, destination, buffer, 0L)
    } yield total

  def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
    for {
      amount <- IO(origin.read(buffer, 0, buffer.length))
      count <- if (amount > -1)
                // 'first >> second' is equivalent to 'first.flatMap(_ => second)'
                IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
              else
                IO.pure(acc) // End of read stream reached (by java.io.InputStream contract), nothing to write
    } yield count            // Returns the actual amount of bytes transmitted

  def inputOutputStreams(in: File, out: File, guard: Semaphore[IO]): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in, guard)
      outStream <- outputStream(out, guard)
    } yield (inStream, outStream)

  def inputStream(f: File, guard: Semaphore[IO]): Resource[IO, FileInputStream] =
    Resource.make {
      IO(new FileInputStream(f)) // build
    } { inStream =>
      guard.withPermit {
        IO(inStream.close()).handleErrorWith(_ => IO(println("Closing input failed"))) // release
      }
    }

  def outputStream(f: File, guard: Semaphore[IO]): Resource[IO, FileOutputStream] =
    Resource.make {
      IO(new FileOutputStream(f)) // build
    } { outStream =>
      guard.withPermit {
        IO(outStream.close()).handleErrorWith(_ => IO(println("Closing output failed"))) // release
      }
    }
}
