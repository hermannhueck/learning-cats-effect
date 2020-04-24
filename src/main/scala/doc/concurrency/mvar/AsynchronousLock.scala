package doc.concurrency.mvar

import cats.effect._
import cats.effect.concurrent._
import cats.syntax.flatMap._
import cats.syntax.functor._

import scala.concurrent.ExecutionContext

/*
  abstract class MVar[F[_], A] {
    def put(a: A): F[Unit]
    def take: F[A]
    def read: F[A]

    def tryPut(a: A): F[Boolean]
    def tryTake: F[Option[A]]
  }
  MVar is ...
  a mutable location that can be empty or contain a value, asynchronously blocking reads when empty and blocking writes when full.
 */
object AsynchronousLock extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  final class MLock(mvar: MVar[IO, Unit]) {

    def acquire: IO[Unit] = mvar.take

    def release: IO[Unit] = mvar.put(())

    def greenLight[A](fa: IO[A]): IO[A] = acquire.bracket(_ => fa)(_ => release)
  }

  object MLock {
    def apply(): IO[MLock] = MVar[IO].of(()).map(ref => new MLock(ref))
  }

  (for {
    lock  <- MLock.apply()
    green <- lock.greenLight(IO("Green Light"))
    _     <- IO(println(green))
  } yield ()).unsafeRunSync()

  println("-----\n")
}
