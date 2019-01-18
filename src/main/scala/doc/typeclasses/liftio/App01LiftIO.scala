package doc.typeclasses.liftio

import cats.effect.{IO, LiftIO}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import cats.data.EitherT
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global

import scala.language.postfixOps

object App01LiftIO extends App {

  println("\n-----")

  type MyEffect[A] = Future[Either[Throwable, A]]

  implicit def myEffectLiftIO: LiftIO[MyEffect] =
    new LiftIO[MyEffect] {
      override def liftIO[A](ioa: IO[A]): MyEffect[A] = {
        ioa.attempt.unsafeToFuture()
      }
    }

  val ioa: IO[String] = IO("Hello World!")
  val effect: MyEffect[String] = LiftIO[MyEffect].liftIO(ioa)

  val either1: Either[Throwable, String] = Await.result(effect, 1 second)
  println(either1)


  val L: LiftIO[MyEffect] = implicitly[LiftIO[MyEffect]]

  val service1: MyEffect[Int] = Future.successful(Right(22))
  val service2: MyEffect[Boolean] = Future.successful(Right(true))
  val service3: MyEffect[String] = Future.successful(Left(new Exception("boom!")))

  val program: MyEffect[String] =
    (for {
      n <- EitherT(service1)
      x <- EitherT(service2)
      y <- EitherT(if (x) L.liftIO(IO("from io")) else service3)
    } yield y).value

  val either2: Either[Throwable, String] = Await.result(program, 1 second)
  println(either2)

  println("-----\n")
}
