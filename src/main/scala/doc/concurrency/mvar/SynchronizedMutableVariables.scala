package doc.concurrency.mvar

import cats.effect._
import cats.effect.concurrent._
import cats.syntax.all._

import scala.concurrent.ExecutionContext

/*
  abstract class MVar[F[_], A] {
    def put(a: A): F[Unit]
    def take: F[A]
    def read: F[A]

    def tryPut(a: A): F[Boolean]
    def tryTake: F[Option[A]]
  }
  MVar is ...
  a mutable location that can be empty or contain a value, asynchronously blocking reads when empty and blocking writes when full.
 */
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
