package doc.datatypes.ioapp

import cats.data.EitherT
import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp}

object App04MainOtherEffects extends IOApp {

  type F[A] = EitherT[IO, Throwable, A]
  val F: ConcurrentEffect[F] = implicitly

  def run(args: List[String]): IO[ExitCode] = F.toIO {
    val et: EitherT[IO, Throwable, ExitCode] =
      EitherT.right(IO(println("\nHello from EitherT\n"))).map(_ => ExitCode.Success)
    et
  }
}