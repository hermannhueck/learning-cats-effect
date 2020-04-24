/*
  See: https://lewisjkl.com/cats-effect-ref/
 */
package doc.concurrency.ref.lewisjkl

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import cats.effect.concurrent.Ref

object Ex02RefSet extends IOApp {

  val printRef = for {
    ref      <- Ref[IO].of(42)
    _        <- ref.set(21)
    contents <- ref.get
  } yield println(contents)

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      _ <- IO(println("\u2500" * 50))
      _ <- printRef
      _ <- IO(println("\u2500" * 50))
    } yield ()).as(ExitCode.Success)
}
