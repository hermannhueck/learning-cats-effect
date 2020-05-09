package ioMonadForCats.simpleio

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object MySimpleIO extends hutil.App {

  case class IO[A](run: () => A) {

    def map[B](f: A => B): IO[B] = IO { () => f(run()) }

    def flatMap[B](f: A => IO[B]): IO[B] = f(run())
  }

  println("\n-----")

  val program = for {
    _    <- IO { () => print("Welcome to Scala!  What's your name?   ") }
    name <- IO { () => scala.io.StdIn.readLine }
    _    <- IO { () => println(s"Well hello, $name!") }
  } yield ()

  program.run()
}
