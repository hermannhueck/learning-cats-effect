/*
  See: https://lewisjkl.com/cats-effect-ref/
 */
package doc.concurrency.ref.lewisjkl

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import cats.effect.concurrent.Ref
import cats.data.NonEmptyList
import cats.syntax.parallel._

object Ex03aRefUpdateWrong extends hutil.IOApp {

  def getThenSet(ref: Ref[IO, Int]): IO[Unit] = {
    ref.get.flatMap { contents => ref.set(contents + 1) }
  }

  val printRef = for {
    ref      <- Ref[IO].of(42)
    _        <- NonEmptyList.of(getThenSet(ref), getThenSet(ref)).parSequence
    contents <- ref.get
    _        <- IO(println(s"prints $contents, may print 43, expected: 44"))
  } yield println(contents)
  // prints 43, expected: 44

  override def ioRun(args: List[String]): IO[ExitCode] =
    for {
      _ <- printRef
    } yield ExitCode.Success
}
