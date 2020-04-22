package doc.datatypes.resource

import java.io.{BufferedReader, File, FileReader}
import cats.effect.{Blocker, ContextShift, IO, Resource, Sync}
import cats.syntax.flatMap._
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

object App04bResourceParametric extends App {

  println("\n-----")

  def reader[F[_]](
      file: File,
      blocker: Blocker
  )(implicit F: Sync[F], cs: ContextShift[F]): Resource[F, BufferedReader] =
    Resource.fromAutoCloseableBlocking(blocker)(F.delay {
      new BufferedReader(new FileReader(file))
    })

  def dumpResource[F[_]](
      res: Resource[F, BufferedReader],
      blocker: Blocker
  )(implicit F: Sync[F], cs: ContextShift[F]): F[Unit] = {
    def loop(in: BufferedReader): F[Unit] =
      F.suspend {
        blocker.delay(in.readLine()).flatMap { line =>
          if (line != null) {
            System.out.println(line)
            loop(in)
          } else {
            F.unit
          }
        }
      }
    res.use(loop)
  }

  def dumpFile[F[_]](file: File, blocker: Blocker)(implicit F: Sync[F], cs: ContextShift[F]): F[Unit] =
    dumpResource(reader(file, blocker), blocker)

  val ec: ExecutionContext          = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  val blocker = Blocker.liftExecutorService(Executors.newCachedThreadPool)

  dumpFile[IO](new File("README.md"), blocker).unsafeRunSync()

  println("-----\n")
}
