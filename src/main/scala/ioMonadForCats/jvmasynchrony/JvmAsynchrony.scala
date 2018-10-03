package ioMonadForCats.jvmasynchrony

import cats.effect.IO

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object JvmAsynchrony extends App {

  println("\n----- Asynchrony and the JVM")

  trait Response[T] {
    def onError(t: Throwable): Unit
    def onSuccess(t: T): Unit
  }

  trait Channel {
    def sendBytes(chunk: Array[Byte], handler: Response[Unit]): Unit
    def receiveBytes(handler: Response[Array[Byte]]): Unit
  }

  def send(c: Channel, chunk: Array[Byte]): IO[Unit] = {
    IO async { cb =>
      c.sendBytes(chunk, new Response[Unit] {
        def onError(t: Throwable): Unit = cb(Left(t))
        def onSuccess(v: Unit): Unit = cb(Right(()))
      })
    }
  }

  def receive(c: Channel): IO[Array[Byte]] = {
    IO async { cb =>
      c.receiveBytes(new Response[Array[Byte]] {
        def onError(t: Throwable): Unit = cb(Left(t))
        def onSuccess(chunk: Array[Byte]): Unit = cb(Right(chunk))
      })
    }
  }

  val c: Channel = null // pretend this is an actual channel

  val io1: IO[Unit] = for {
    _ <- send(c, "SYN".getBytes)
    response <- receive(c)

    _ <- if (response sameElements "ACK".getBytes)   // pretend == works on Array[Byte]
      IO { println("found the guy!") }
    else
      IO { println("no idea what happened, but it wasn't good") }
  } yield ()

  println("----- Thread Shifting")

  import scala.concurrent._
  implicit val ec: ExecutionContext = ExecutionContext.global

  val io2: IO[Unit] = for {
    _ <- send(c, "SYN".getBytes)
    response <- receive(c)
    _ <- IO.shift(ec)   // there's no place like home!

    _ <- if (response sameElements "ACK".getBytes)   // pretend == works on Array[Byte]
      IO { println("found the guy!") }
    else
      IO { println("no idea what happened, but it wasn't good") }
  } yield ()

  println("-----\n")
}
