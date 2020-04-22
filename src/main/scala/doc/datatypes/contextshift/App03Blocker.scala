package doc.datatypes.contextshift

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect._

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object App03Blocker extends IOApp {

  def readName[F[_]: Sync: ContextShift](blocker: Blocker): F[String] =
    // Blocking operation, executed on special thread-pool
    blocker.delay {
      print("Enter your name: ")
      scala.io.StdIn.readLine().trim
    }
  def run(args: List[String]): IO[ExitCode] = {

    val name: IO[String] = Blocker[IO].use { blocker => readName[IO](blocker) }

    for {
      _ <- IO(println("\n-----"))
      n <- name
      _ <- IO(println(s"\nHello, $n!"))
      _ <- IO(println("-----\n"))
    } yield ExitCode.Success
  }
}
