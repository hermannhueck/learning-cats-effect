package doc.concurrency.mvar

import cats.effect.IO
import cats.effect.concurrent.MVar

import scala.concurrent.ExecutionContext
import cats.effect.ContextShift

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
object ProducerConsumerChannel extends hutil.App {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  type Channel[A] = MVar[IO, Option[A]]

  def producer(ch: Channel[Int], list: List[Int]): IO[Unit] =
    list match {
      case Nil =>
        ch.put(None) // we are done!
      case head :: tail =>
        // next please
        ch.put(Some(head)).flatMap(_ => producer(ch, tail))
    }

  def consumer(ch: Channel[Int], sum: Long): IO[Long] =
    ch.take.flatMap {
      case Some(x) =>
        // next please
        consumer(ch, sum + x)
      case None =>
        IO.pure(sum) // we are done!
    }

  val prog = for {
    channel      <- MVar[IO].empty[Option[Int]]
    count        = 100000
    producerTask = producer(channel, (0 until count).toList)
    consumerTask = consumer(channel, 0L)

    fp  <- producerTask.start
    fc  <- consumerTask.start
    _   <- fp.join
    sum <- fc.join
  } yield sum

  prog.map(println).unsafeRunSync()
}
