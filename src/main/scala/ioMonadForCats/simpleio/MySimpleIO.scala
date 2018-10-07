package ioMonadForCats.simpleio

import mycats.effect.IO

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object MySimpleIO extends App {

  println("\n-----")

  val program = for {
    _    <- IO { () => print("Welcome to Scala!  What's your name?   ") }
    name <- IO { () => scala.io.StdIn.readLine }
    _    <- IO { () => println(s"Well hello, $name!") }
  } yield ()

  program.run()

  println("-----\n")
}