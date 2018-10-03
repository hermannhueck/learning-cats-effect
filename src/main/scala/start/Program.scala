package start

import cats.effect.IO

object Program extends App {

  val program = for {
    _    <- IO { print("Welcome to Scala!  What's your name?   ") }
    name <- IO { scala.io.StdIn.readLine }
    _    <- IO { println(s"Well hello, $name!") }
  } yield ()

  println("\n-----")
  program.unsafeRunSync()
  println("-----\n")
}