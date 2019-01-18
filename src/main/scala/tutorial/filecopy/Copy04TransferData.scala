package tutorial.filecopy

import java.io._

import cats.effect.{IO, Resource}

import cats.syntax.functor._
import cats.syntax.flatMap._

/*
  See tutorial and solutions at:
  https://typelevel.org/cats-effect/tutorial/tutorial.html#tcp-echo-server---concurrent-system-with-fibers
  https://github.com/lrodero/cats-effect-tutorial
 */
object Copy04TransferData extends App {

  println("\n-----")

  val usage = s"Usage: ${Copy01Resource.getClass.getSimpleName.replaceFirst(".$", "")} inputFile outputFile"

  runFileCopy(args.toList).map(println).unsafeRunSync()

  def runFileCopy(args: List[String]): IO[String] = args match {

    case input :: output :: rest =>
      val origin = new File(input)
      val destination = new File(output)
      if (!origin.exists())
        IO.raiseError[String](new FileNotFoundException(s"Input file doesn't exist: ${origin.getPath}"))
      else
        copy(origin, destination)
          .map(size => s"$size bytes copied from $input to $output")

    case input :: rest =>
      IO("Error: outputFile not specified\n" + usage)

    case Nil =>
      IO(usage)
  }

  def copy(origin: File, destination: File): IO[Long] =
    inputOutputStreams(origin, destination).use { case (in, out) => transfer(in, out) }

  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
    for {
      buffer <- IO(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
      total <- transmit(origin, destination, buffer, 0L)
    } yield total

  def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
    for {
      amount <- IO(origin.read(buffer, 0, buffer.length))
      count  <-
        if (amount > -1)
          // 'first >> second' is equivalent to 'first.flatMap(_ => second)'
          IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
        else
          IO.pure(acc) // End of read stream reached (by java.io.InputStream contract), nothing to write
    } yield count // Returns the actual amount of bytes transmitted

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
        IO(new FileInputStream(f)) // build
      } { inStream =>
        IO(inStream.close()).handleErrorWith(_ => IO(println("Closing input failed"))) // release
      }

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.make {
        IO(new FileOutputStream(f)) // build
      } { outStream =>
        IO(outStream.close()).handleErrorWith(_ => IO(println("Closing output failed"))) // release
      }

  println("-----\n")
}
