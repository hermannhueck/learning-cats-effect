package doc.datatypes.syncio

import cats.Eval
import cats.effect.SyncIO

object App02SyncIO extends App {

  println("\n-----")

  val eval = Eval.now("hey!")
  // eval: cats.Eval[String] = Now(hey!)

  val str = SyncIO.eval(eval).unsafeRunSync
  println(str)
  // hey!
  
  println("-----\n")
}
