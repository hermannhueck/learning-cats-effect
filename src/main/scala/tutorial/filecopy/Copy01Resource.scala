package tutorial.filecopy

import java.io._

import cats.effect.{IO, Resource}

import cats.syntax.functor._

/*
  See tutorial and solutions at:
  https://typelevel.org/cats-effect/tutorial/tutorial.html#tcp-echo-server---concurrent-system-with-fibers
  https://github.com/lrodero/cats-effect-tutorial
 */
object Copy01Resource extends App {

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

  def transfer(origin: InputStream, destination: OutputStream): IO[Long] = IO {
    // transfer in classic Java manner (to be changed later)
    var total = 0L
    val buffer: Array[Byte] = new Array[Byte](1024 * 10)
    var amount = origin.read(buffer)
    while (amount > 0) {
      destination.write(buffer, 0, amount)
      total += amount
      amount = origin.read(buffer)
    }
    total
  }

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
        IO(outStream.close()).handleErrorWith(_ => IO(println("Closing input failed"))) // release
      }

  println("-----\n")
}