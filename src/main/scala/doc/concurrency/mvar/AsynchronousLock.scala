package doc.concurrency.mvar

import cats.effect._
import cats.effect.concurrent._
import cats.syntax.all._

import scala.concurrent.ExecutionContext

object AsynchronousLock extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  final class MLock(mvar: MVar[IO, Unit]) {
    def acquire: IO[Unit] =
      mvar.take

    def release: IO[Unit] =
      mvar.put(())

    def greenLight[A](fa: IO[A]): IO[A] =
      acquire.bracket(_ => fa)(_ => release)
  }

  object MLock {
    def apply(): IO[MLock] =
      MVar[IO].empty[Unit].map(ref => new MLock(ref))
  }

  println("-----\n")
}
