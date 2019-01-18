package doc.datatypes.resource

import java.io.{BufferedReader, File, FileReader}

import cats.effect.{IO, Resource, Sync}

import scala.language.higherKinds

object App03Resource extends App {

  println("\n-----")

  def reader[F[_]](file: File)(implicit F: Sync[F]): Resource[F, BufferedReader] =
    Resource.fromAutoCloseable(F.delay {
      new BufferedReader(new FileReader(file))
    })

  def dumpResource[F[_]](res: Resource[F, BufferedReader])(implicit F: Sync[F]): F[Unit] = {
    def loop(in: BufferedReader): F[Unit] =
      F.suspend {
        val line = in.readLine()
        if (line != null) {
          System.out.println(line)
          loop(in)
        } else {
          F.unit
        }
      }
    res.use(loop)
  }

  def dumpFile[F[_]](file: File)(implicit F: Sync[F]): F[Unit] =
    dumpResource(reader(file))

  dumpFile[IO](new File("README.md")).unsafeRunSync()

  println("-----\n")
}
