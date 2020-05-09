package doc.datatypes.io

import cats.data._
import cats.syntax.parallel._
import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext

object App14dParSequence extends hutil.App {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val anIO = IO(1)

  val aLotOfIOs: NonEmptyList[IO[Int]] = NonEmptyList.of(anIO, anIO)

  // parSequence requires anm implicit ContextShift
  val ioOfList: IO[NonEmptyList[Int]] = aLotOfIOs.parSequence

  ioOfList.map(println).unsafeRunSync()
}
