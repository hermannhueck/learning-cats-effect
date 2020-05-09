package doc.datatypes.io

import cats.data._
import cats.effect.IO

object App14eTraverse extends hutil.App {

  val results: IO[NonEmptyList[Int]] = NonEmptyList.of(1, 2, 3).traverse { i => IO(i) }

  results.map(println).unsafeRunSync()
}
