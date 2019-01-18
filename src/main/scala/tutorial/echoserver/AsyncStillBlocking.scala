package tutorial.echoserver

import cats.effect._
import cats.effect.syntax.all._
import cats.implicits._
import scala.util.Either
import scala.language.higherKinds

/*
  See tutorial and solutions at:
  https://typelevel.org/cats-effect/tutorial/tutorial.html#tcp-echo-server---concurrent-system-with-fibers
  https://github.com/lrodero/cats-effect-tutorial
 */
object AsyncStillBlocking extends App {

  type Callback[A] = Either[Throwable, A] => Unit

  def delayed[F[_]: Async]: F[Unit] = for {
    _ <- Sync[F].delay(println("Starting")) // Async extends Sync, so (F[_]: Async) 'brings' (F[_]: Sync)
    _ <- Async[F].async{ cb: Callback[Unit] =>
      println("Async.async ...")
      Thread.sleep(2000)
      println("... Async.async done")
      cb(Right(()))
    }
    _ <- Sync[F].delay(println("Done")) // 2 seconds to get here, no matter what, as we were 'blocked' by previous call
  } yield()

  delayed[IO].unsafeRunSync() // a way to run an IO without IOApp
}
