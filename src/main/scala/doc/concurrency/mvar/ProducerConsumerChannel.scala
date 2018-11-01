package doc.concurrency.mvar

import cats.effect._
import cats.effect.concurrent._

import scala.concurrent.ExecutionContext

object ProducerConsumerChannel extends App {

  println("\n-----")

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
    channel <- MVar[IO].empty[Option[Int]]
    count = 100000
    producerTask = producer(channel, (0 until count).toList)
    consumerTask = consumer(channel, 0L)

    fp  <- producerTask.start
    fc  <- consumerTask.start
    _   <- fp.join
    sum <- fc.join
  } yield sum

  prog.map(println).unsafeRunSync()

  println("-----\n")
}
