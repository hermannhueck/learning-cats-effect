package doc.datatypes.io

import cats.effect.IO

object App04SyncEffects extends App {

  println("\n-----")

  def putStrlLn(value: String) = IO(println(value))
  val readLn = IO(scala.io.StdIn.readLine)

  val program: IO[Unit] =
    for {
      _ <- putStrlLn("What's your name?")
      n <- readLn
      _ <- putStrlLn(s"Hello, $n!")
    } yield ()

  program.unsafeRunSync()

  println("-----\n")
}
