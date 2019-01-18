package doc.datatypes.io

import cats.effect.{ContextShift, IO}
import cats.syntax.parallel._

import scala.concurrent.ExecutionContext

object App14aParMapN extends App {

  println("\n-----")

  val ioA = IO(println("Running ioA"))
  val ioB = IO(println("Running ioB"))
  val ioC = IO(println("Running ioC"))

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val program = (ioA, ioB, ioC).parMapN { (_, _, _) => () }

  program.unsafeRunSync()
  //=> Running ioB
  //=> Running ioC
  //=> Running ioA

  println("-----\n")
}
