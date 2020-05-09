package doc.datatypes.io

import cats.data._
import cats.effect.IO
import cats.syntax.traverse._

object App14dSequence extends hutil.App {

  val anIO = IO(1)

  val aLotOfIOs: NonEmptyList[IO[Int]] = NonEmptyList.of(anIO, anIO)

  val ioOfList: IO[NonEmptyList[Int]] = aLotOfIOs.sequence

  ioOfList.map(println).unsafeRunSync()
}
