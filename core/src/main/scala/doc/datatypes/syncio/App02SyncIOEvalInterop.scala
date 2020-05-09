package doc.datatypes.syncio

import cats.Eval
import cats.effect.SyncIO

object App02SyncIOEvalInterop extends hutil.App {

  val eval = Eval.now("Hey! Eval me!")
  // eval: cats.Eval[String] = Now(hey!)

  val str = SyncIO.eval(eval).unsafeRunSync
  println(str)
  // Hey! Eval me!
}
