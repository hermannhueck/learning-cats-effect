package tutorial.echoserver

import java.io._
import java.net._

import cats.effect.ExitCase._
import cats.effect._
import cats.effect.concurrent.MVar
import cats.effect.syntax.all._
import cats.implicits._

/*
  See tutorial and solutions at:
  https://typelevel.org/cats-effect/tutorial/tutorial.html#tcp-echo-server---concurrent-system-with-fibers
  https://github.com/lrodero/cats-effect-tutorial
 */
object EchoServer02GracefulStop extends hutil.IOApp {

  def echoProtocol[F[_]: Sync](clientSocket: Socket, stopFlag: MVar[F, Unit]): F[Unit] = {

    def loop(reader: BufferedReader, writer: BufferedWriter): F[Unit] =
      for {
        line <- Sync[F].delay(reader.readLine())
        _ <- line match {
              case stop if stop.toUpperCase == "STOP" =>
                Sync[F].delay(println(s"GOT: $line")) >>
                  stopFlag.put(()) // Stopping server! Also put(()) returns F[Unit] which is handy as we are done
              case "" => Sync[F].unit // Empty line, we are done
              case _ =>
                Sync[F].delay {
                  println(s"GOT: $line"); writer.write(line); writer.newLine(); writer.flush()
                } >> loop(reader, writer)
            }
      } yield ()

    def reader(clientSocket: Socket): Resource[F, BufferedReader] =
      Resource.make {
        Sync[F].delay(new BufferedReader(new InputStreamReader(clientSocket.getInputStream)))
      } { reader =>
        Sync[F].delay(reader.close()).handleErrorWith(_ => Sync[F].delay(println("Failed to close BufferedReader")))
      }

    def writer(clientSocket: Socket): Resource[F, BufferedWriter] =
      Resource.make {
        Sync[F].delay(new BufferedWriter(new PrintWriter(clientSocket.getOutputStream)))
      } { writer =>
        Sync[F].delay(writer.close()).handleErrorWith(_ => Sync[F].delay(println("Failed to close BufferedWriter")))
      }

    def readerWriter(clientSocket: Socket): Resource[F, (BufferedReader, BufferedWriter)] =
      for {
        reader <- reader(clientSocket)
        writer <- writer(clientSocket)
      } yield (reader, writer)

    readerWriter(clientSocket).use {
      case (reader, writer) =>
        loop(reader, writer) // Let's get to work
    }
  }

  def serve[F[_]: Concurrent](serverSocket: ServerSocket, stopFlag: MVar[F, Unit]): F[Unit] = {

    def close(socket: Socket): F[Unit] =
      Sync[F].delay(socket.close()).handleErrorWith(_ => Sync[F].delay(println("Failed to close socket")))

    def startEchoFiber(socket: Socket): F[Fiber[F, Unit]] =
      echoProtocol(socket, stopFlag)
        .guarantee(close(socket)) // Ensuring socket is closed
        .start

    for {
      _ <- Sync[F]
            .delay(serverSocket.accept())
            .bracketCase { socket =>
              startEchoFiber(socket) // Will run in its own Fiber!
            } { (socket, exit) =>
              exit match {
                case Completed           => Sync[F].unit
                case Error(_) | Canceled => close(socket)
              }
            }
      _ <- serve(serverSocket, stopFlag) // Looping back to the beginning
    } yield ()
  }

  def server[F[_]: Concurrent](serverSocket: ServerSocket): F[ExitCode] =
    for {
      stopFlag    <- MVar[F].empty[Unit]
      serverFiber <- serve(serverSocket, stopFlag).start // Server runs on its own Fiber
      _           <- stopFlag.read // Blocked until 'stopFlag.put(())' is run
      _           <- serverFiber.cancel // Stopping server!
    } yield ExitCode.Success

  override def ioRun(args: List[String]): IO[ExitCode] = {

    def close[F[_]: Sync](socket: ServerSocket): F[Unit] =
      Sync[F].delay(socket.close()).handleErrorWith(_ => Sync[F].delay(println("Failed to close server socket")))

    IO(new ServerSocket(args.headOption.map(_.toInt).getOrElse(5432)))
      .bracket { serverSocket => server[IO](serverSocket) >> IO.pure(ExitCode.Success) } { serverSocket =>
        close[IO](serverSocket) >> IO(println("Server finished"))
      }
  }
}
