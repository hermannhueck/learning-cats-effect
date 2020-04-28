package ioMonadForCats.rt

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTrans extends App {

  println("\n-----")

  @scala.annotation.nowarn("cat=other-pure-statement&msg=a pure expression does nothing in statement position")
  def f(ioa1: Unit, ioa2: Unit): Unit = {
    ioa1
    ioa2
  }

  f(println("hi"), println("hi"))
  println("-----")
  // isn't really equivalent to!
  val x: Unit = println("hi")
  f(x, x)

  println("-----\n")
}
