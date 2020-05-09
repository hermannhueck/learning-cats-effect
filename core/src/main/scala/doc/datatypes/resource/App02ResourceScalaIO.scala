package doc.datatypes.resource

import java.io.{BufferedReader, File, FileReader}

import cats.effect.{IO, Resource}

import scala.collection.JavaConverters._

object App02ResourceScalaIO extends hutil.App {

  val acquire = IO {
    scala.io.Source.fromString("Hello world")
  }

  val ioa: IO[Unit] = Resource.fromAutoCloseable(acquire).use(source => IO(println(source.mkString)))

  ioa.unsafeRunSync()
}
