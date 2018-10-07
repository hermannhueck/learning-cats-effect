package ioMonadForCats.rt

import cats.effect.IO

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTrans2 extends App {

  println("\n-----")

  def putStrLn(line: String): IO[Unit] =
    IO { println(line) }

  def f(ioa1: IO[Unit], ioa2: IO[Unit]): IO[Unit] = for {
    _ <- ioa1
    _ <- ioa2
  } yield ()

  f(putStrLn("hi"), putStrLn("hi")).unsafeRunSync()
  println("-----")
  // isn't really equivalent to!
  val x: IO[Unit] = putStrLn("hi")
  f(x, x).unsafeRunSync()

  println("-----\n")
}
