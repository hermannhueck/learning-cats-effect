/*
  See: https://lewisjkl.com/cats-effect-ref/
 */
package doc.concurrency.ref.lewisjkl

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import cats.effect.concurrent.Ref

object Ex04RefModify extends hutil.IOApp {

  val printRef = for {
    ref      <- Ref[IO].of(42)
    contents <- ref.modify(current => (current + 20, current + 20))
  } yield println(contents)

  override def ioRun(args: List[String]): IO[ExitCode] =
    for {
      _ <- printRef
    } yield ExitCode.Success
}
