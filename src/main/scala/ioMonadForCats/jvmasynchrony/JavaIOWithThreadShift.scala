package ioMonadForCats.jvmasynchrony

import java.util.concurrent.Executors

import cats.effect.IO

import scala.concurrent.{ExecutionContext}

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object JavaIOWithThreadShift extends App {

  def readLines(name: String): IO[Vector[String]] = IO {

    import java.io.{BufferedReader, FileReader}

    val reader = new BufferedReader(new FileReader(name))
    var back: Vector[String] = Vector.empty

    try {
      var line: String = null
      do {
        line = reader.readLine()
        back = back :+ line
      } while (line != null)
    } finally {
      reader.close()
    }

    back.init
  }

  def getOutput(name: String, lines: Vector[String]): String =
    if (lines.contains(name)) "You're on the list, boss." else "Get outa here!"

  println("\n----- Java IO without Thread Shift")

  val io1: IO[Unit] = for {
    _ <- IO { print("Name, please: ") }
    name <- IO { scala.io.StdIn.readLine }
    lines <- readLines("names.txt")
    _ <- IO { println(getOutput(name, lines)) }
  } yield ()

  io1.unsafeRunSync()

  println("\n----- Java IO with Thread Shift")

  val Main: ExecutionContext = ExecutionContext.global
  val BlockingFileIO: ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  val io: IO[Unit] = for {
    _ <- IO { print("Name, please: ") }
    _ <- IO.shift(BlockingFileIO)
    name <- IO { scala.io.StdIn.readLine }
    lines <- readLines("names.txt")
    _ <- IO.shift(Main)
    _ <- IO { println(getOutput(name, lines)) }
  } yield ()

  io.unsafeRunSync()

  System.exit(0)

  println("-----\n")
}
