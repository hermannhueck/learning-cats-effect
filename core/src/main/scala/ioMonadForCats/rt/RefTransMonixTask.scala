package ioMonadForCats.rt

import scala.language.postfixOps

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTransMonixTask extends hutil.App {

  /*
  val task: Task[Int] = Task {println("processing async lazily ..."); 5}
  val taskSquared1: Task[Int] = for {
    res1 <- task
    res2 <- task
  } yield res1 * res2

  taskSquared1 runAsync println

  println("-----")

  val taskSquared2: Task[Int] = for {
    res1 <- Task {println("processing async lazily ..."); 5}
    res2 <- Task {println("processing async lazily ..."); 5}
  } yield res1 * res2

  taskSquared2 runAsync println
 */
}
