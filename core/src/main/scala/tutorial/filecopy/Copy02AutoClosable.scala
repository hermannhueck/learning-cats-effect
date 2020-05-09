package tutorial.filecopy

import java.io._

import cats.effect.{IO, Resource}

import cats.syntax.functor._

/*
  See tutorial and solutions at:
  https://typelevel.org/cats-effect/tutorial/tutorial.html#tcp-echo-server---concurrent-system-with-fibers
  https://github.com/lrodero/cats-effect-tutorial
 */
object Copy02AutoClosable extends hutil.App {

  val usage = s"Usage: ${Copy02AutoClosable.getClass.getSimpleName.replaceFirst(".$", "")} inputFile outputFile"

  runFileCopy(args.toList).map(println).unsafeRunSync()

  def runFileCopy(args: List[String]): IO[String] = args match {

    case input :: output :: _ =>
      val origin      = new File(input)
      val destination = new File(output)
      if (!origin.exists())
        IO.raiseError[String](new FileNotFoundException(s"Input file doesn't exist: ${origin.getPath}"))
      else
        copy(origin, destination)
          .map(size => s"$size bytes copied from $input to $output")

    case Nil =>
      IO(usage)

    case _ =>
      IO("Error: outputFile not specified\n" + usage)
  }

  def copy(origin: File, destination: File): IO[Long] =
    inputOutputStreams(origin, destination).use { case (in, out) => transfer(in, out) }

  def transfer(origin: InputStream, destination: OutputStream): IO[Long] = IO {
    // transfer in classic Java manner (to be changed later)
    var total               = 0L
    val buffer: Array[Byte] = new Array[Byte](1024 * 10)
    var amount              = origin.read(buffer)
    while (amount > 0) {
      destination.write(buffer, 0, amount)
      total += amount
      amount = origin.read(buffer)
    }
    total
  }

  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.fromAutoCloseable(IO(new FileInputStream(f)))

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.fromAutoCloseable(IO(new FileOutputStream(f)))
}
