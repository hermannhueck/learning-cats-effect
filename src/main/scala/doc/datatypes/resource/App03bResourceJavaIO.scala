package doc.datatypes.resource

import java.io.{BufferedReader, File, FileReader}

import cats.effect.{Blocker, IO, Resource}

// import scala.collection.JavaConverters._ // deprecated since 2.13.0
import scala.jdk.CollectionConverters._
import cats.effect.ContextShift
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object App03bResourceJavaIO extends App {

  println("\n-----")

  def readAllLines(bufferedReader: BufferedReader, blocker: Blocker)(implicit cs: ContextShift[IO]): IO[List[String]] =
    blocker.delay[IO, List[String]] {
      bufferedReader.lines().iterator().asScala.toList
    }

  def reader(file: File, blocker: Blocker)(implicit cs: ContextShift[IO]): Resource[IO, BufferedReader] =
    Resource.fromAutoCloseableBlocking(blocker)(IO {
      new BufferedReader(new FileReader(file))
    })

  def readLinesFromFile(file: File, blocker: Blocker)(implicit cs: ContextShift[IO]): IO[List[String]] =
    reader(file, blocker).use(br => readAllLines(br, blocker))

  val ec: ExecutionContext          = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  val blocker = Blocker.liftExecutorService(Executors.newCachedThreadPool)

  val ioa: IO[Unit] = for {
    lines <- readLinesFromFile(new File("README.md"), blocker)
    _     <- IO { lines foreach println }
  } yield ()

  ioa.unsafeRunSync()

  println("-----\n")
}
