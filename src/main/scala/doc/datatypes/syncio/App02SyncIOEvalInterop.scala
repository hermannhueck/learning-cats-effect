package doc.datatypes.syncio

import cats.Eval
import cats.effect.SyncIO

object App02SyncIOEvalInterop extends App {

  println("\n-----")

  val eval = Eval.now("Hey! Eval me!")
  // eval: cats.Eval[String] = Now(hey!)

  val str = SyncIO.eval(eval).unsafeRunSync
  println(str)
  // Hey! Eval me!
  
  println("-----\n")
}
