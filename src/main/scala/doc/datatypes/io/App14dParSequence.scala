package doc.datatypes.io

import cats.data._
import cats.syntax.parallel._
import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext

object App14dParSequence extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val anIO = IO(1)

  val aLotOfIOs = NonEmptyList.of(anIO, anIO)

  // parSequence requires anm implicit ContextShift
  val ioOfList = aLotOfIOs.parSequence

  ioOfList.map(println).unsafeRunSync()

  println("-----\n")
}
