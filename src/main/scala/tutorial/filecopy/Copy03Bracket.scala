package tutorial.filecopy

import java.io._

import cats.effect.IO

import cats.syntax.functor._
import cats.syntax.apply._

/*
  See tutorial and solutions at:
  https://typelevel.org/cats-effect/tutorial/tutorial.html#tcp-echo-server---concurrent-system-with-fibers
  https://github.com/lrodero/cats-effect-tutorial
 */
object Copy03Bracket extends App {

  println("\n-----")

  val usage = s"Usage: ${Copy02AutoClosable.getClass.getSimpleName.replaceFirst(".$", "")} inputFile outputFile"

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

  def copy(origin: File, destination: File): IO[Long] = {

    val inIO: IO[InputStream]  = IO(new FileInputStream(origin))
    val outIO:IO[OutputStream] = IO(new FileOutputStream(destination))
    // Problem!!!
    // !!! When opening the output file fails, the InputStream will not be closed !!!
    // This problem is solved when using Resource.

    (inIO, outIO)              // Stage 1: Getting resources
      .tupled                  // From (IO[InputStream], IO[OutputStream]) to IO[(InputStream, OutputStream)]
      .bracket{
      case (in, out) =>
        transfer(in, out)    // Stage 2: Using resources (for copying data, in this case)
    } {
      case (in, out) =>      // Stage 3: Freeing resources
        (IO(in.close()), IO(out.close()))
          .tupled              // From (IO[Unit], IO[Unit]) to IO[(Unit, Unit)]
          .handleErrorWith(_ => IO.unit).void
    }
  }

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

  println("-----\n")
}
