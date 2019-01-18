package doc.datatypes.io

import cats.data._
import cats.effect.{ContextShift, IO}
import cats.syntax.parallel._

import scala.concurrent.ExecutionContext

object App14eParTraverse extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  // parTraverse requires anm implicit ContextShift
  val results: IO[NonEmptyList[Int]] = NonEmptyList.of(1, 2, 3).parTraverse { i =>
    IO(i)
  }

  results.map(println).unsafeRunSync()

  println("-----\n")
}
