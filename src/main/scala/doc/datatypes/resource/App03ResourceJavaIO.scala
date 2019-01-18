package doc.datatypes.resource

import java.io.{BufferedReader, File, FileReader}

import cats.effect.{IO, Resource}

import scala.collection.JavaConverters._

object App02Resource extends App {

  println("\n-----")

  def readAllLines(bufferedReader: BufferedReader): IO[List[String]] = IO {
    bufferedReader.lines().iterator().asScala.toList
  }

  def reader(file: File): Resource[IO, BufferedReader] =
    Resource.fromAutoCloseable(IO {
      new BufferedReader(new FileReader(file))
    })

  def readLinesFromFile(file: File): IO[List[String]] = {
    reader(file).use(readAllLines)
  }

  val ioa: IO[Unit] = for {
    lines <- readLinesFromFile(new File("README.md"))
    _ <- IO { lines foreach println }
  } yield ()

  ioa.unsafeRunSync()

  println("-----\n")
}
