package doc.datatypes.io

import java.util.concurrent.Executors

import cats.effect.{ContextShift, IO}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object App13bThreadShift extends App {

  println("\n-----")

  val cachedThreadPool = Executors.newCachedThreadPool()
  val BlockingFileIO   = ExecutionContext.fromExecutor(cachedThreadPool)
  implicit val Main: ExecutionContext = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(Main)

  val ioa: IO[Unit] =
    for {
      _     <- IO(println("Enter your name: "))
      _     <- IO.shift(BlockingFileIO)
      name  <- IO(scala.io.StdIn.readLine())
      _     <- IO.shift
      _     <- IO(println(s"Welcome $name!"))
      _     <- IO(cachedThreadPool.shutdown())
    } yield ()

  ioa.unsafeRunSync()

  println("-----\n")
}
