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
object Copy07Polymorphic extends hutil.IOApp {

  // IOApp allows for purely functional code
  // provides implicit instances of Timer[IO] and ContextShift[IO]

  val usage = s"Usage: ${Copy01Resource.getClass.getSimpleName.replaceFirst(".$", "")} inputFile outputFile"

  override def ioRun(args: List[String]): IO[ExitCode] =
    for {
      _ <- if (args.length < 2)
            IO.raiseError(new IllegalArgumentException(s"Need origin and destination files\n$usage"))
          else IO.unit
      //_     <- timer.sleep(3.seconds)
      orig  = new File(args(0))
      dest  = new File(args(1))
      count <- copy[IO](orig, dest) // reify F[_] with IO
      _     <- IO(println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}"))
    } yield ExitCode.Success

  def copy[F[_]: Concurrent](origin: File, destination: File): F[Long] =
    for {
      guard <- Semaphore[F](1)
      count <- inputOutputStreams(origin, destination, guard).use {
                case (in, out) =>
                  guard.withPermit(transfer(in, out))
              }
    } yield count

  def inputOutputStreams[F[_]: Sync](
      in: File,
      out: File,
      guard: Semaphore[F]
  ): Resource[F, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in, guard)
      outStream <- outputStream(out, guard)
    } yield (inStream, outStream)

  def transfer[F[_]: Sync](origin: InputStream, destination: OutputStream): F[Long] =
    for {
      buffer <- Sync[F].delay(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
      total  <- transmit(origin, destination, buffer, 0L)
    } yield total

  def transmit[F[_]: Sync](origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): F[Long] =
    for {
      amount <- Sync[F].delay(origin.read(buffer, 0, buffer.length))
      count <- if (amount > -1)
                // 'first >> second' is equivalent to 'first.flatMap(_ => second)'
                Sync[F].delay(destination.write(buffer, 0, amount)) >>
                  transmit(origin, destination, buffer, acc + amount)
              else
                Sync[F].pure(acc) // End of read stream reached (by java.io.InputStream contract), nothing to write
    } yield count                 // Returns the actual amount of bytes transmitted

  import cats.syntax.applicativeError._ // for handleErrorWith

  def inputStream[F[_]: Sync](f: File, guard: Semaphore[F]): Resource[F, FileInputStream] =
    Resource.make {
      Sync[F].delay(new FileInputStream(f)) // build
    } { inStream =>
      guard.withPermit {
        Sync[F]
          .delay(inStream.close())
          .handleErrorWith(_ => Sync[F].delay(println("Closing input failed"))) // release
      }
    }

  def outputStream[F[_]: Sync](f: File, guard: Semaphore[F]): Resource[F, FileOutputStream] =
    Resource.make {
      Sync[F].delay(new FileOutputStream(f)) // build
    } { outStream =>
      guard.withPermit {
        Sync[F]
          .delay(outStream.close())
          .handleErrorWith(_ => Sync[F].delay(println("Closing output failed"))) // release
      }
    }
}
