package ioMonadForCats.reftrans

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTrans extends App {

  println("\n-----")

  def f(ioaction1: Unit, ioaction2: Unit): Unit = {
    ioaction1
    ioaction2
  }

  f(println("hi"), println("hi"))
  println("-----")
  // isn't really equivalent to!
  val x: Unit = println("hi")
  f(x, x)

  println("-----\n")
}
