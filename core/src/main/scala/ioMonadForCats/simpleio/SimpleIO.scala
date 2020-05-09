package ioMonadForCats.simpleio

import cats.effect.IO

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object SimpleIO extends hutil.App {

  val program = for {
    _    <- IO { print("Welcome to Scala!  What's your name?   ") }
    name <- IO { scala.io.StdIn.readLine }
    _    <- IO { println(s"Well hello, $name!") }
  } yield ()

  program.unsafeRunSync()
}
