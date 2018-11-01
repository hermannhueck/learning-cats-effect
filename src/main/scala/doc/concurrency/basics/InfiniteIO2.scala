package doc.concurrency.basics

import java.util.concurrent.Executors

import cats.effect.{ContextShift, Fiber, IO}

import scala.concurrent.ExecutionContext

object InfiniteIO2 extends App {

  println("\n-----")

  val ecOne = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
  val ecTwo = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  val csOne: ContextShift[IO] = IO.contextShift(ecOne)
  val csTwo: ContextShift[IO] = IO.contextShift(ecTwo)

  def infiniteIO(id: Int)(cs: ContextShift[IO]): IO[Fiber[IO, Unit]] = {

    def repeat: IO[Unit] = IO(println(id)).flatMap(_ => repeat)

    repeat.start(cs)
  }


  val prog =
    for {
      _ <- infiniteIO(1)(csOne)
      _ <- infiniteIO(11)(csOne)
      _ <- infiniteIO(2)(csTwo)
      _ <- infiniteIO(22)(csTwo)
    } yield ()

  prog.unsafeRunSync() // inifinitely prints 1 and 2

  println("-----\n")
}