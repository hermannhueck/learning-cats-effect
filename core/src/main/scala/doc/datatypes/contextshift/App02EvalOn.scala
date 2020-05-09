package doc.datatypes.contextshift

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect._

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object App02EvalOn extends hutil.IOApp {

  def blockingThreadPool[F[_]](implicit F: Sync[F]): Resource[F, ExecutionContext] =
    Resource(F.delay {
      val executor: ExecutorService = Executors.newCachedThreadPool()
      val ec: ExecutionContext      = ExecutionContext.fromExecutor(executor)
      (ec, F.delay(executor.shutdown()))
    })

  def readName[F[_]](implicit F: Sync[F]): F[String] =
    F.delay {
      print("Enter your name: ")
      scala.io.StdIn.readLine().trim
    }

  def ioRun(args: List[String]): IO[ExitCode] = {

    val name: IO[String] = blockingThreadPool[IO].use { ec =>
      // Blocking operation, executed on special thread-pool
      contextShift.evalOn(ec)(readName[IO])
    }

    for {
      n <- name
      _ <- IO(println(s"\nHello, $n!"))
    } yield ExitCode.Success
  }
}
