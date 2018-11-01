package doc.concurrency.mvar

import cats.effect._
import cats.effect.concurrent._
import cats.syntax.all._

import scala.concurrent.ExecutionContext

object SynchronizedMutableVariables extends App {

  println("\n-----")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def sum(state: MVar[IO, Int], list: List[Int]): IO[Int] =
    list match {
      case Nil => state.take
      case x :: xs =>
        state.take.flatMap { current =>
          state.put(current + x).flatMap(_ => sum(state, xs))
        }
    }

  val prog = MVar.of[IO, Int](0).flatMap(sum(_, (0 until 100).toList))

  prog.map(println).unsafeRunSync()

  println("-----\n")
}
