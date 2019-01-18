package doc.datatypes.resource

import cats.effect.{IO, Resource}
import cats.syntax.apply._
import cats.syntax.functor._

object App01Resource extends App {

  println("\n-----")

  def mkResource(s: String): Resource[IO, String] = {
    val acquire = IO(println(s"Acquiring $s")) *> IO.pure(s)

    def release(s: String) = IO(println(s"Releasing $s"))

    Resource.make(acquire)(release)
  }

  val r: Resource[IO, (String, String)] = for {
    outer <- mkResource("outer")
    inner <- mkResource("inner")
  } yield (outer, inner)

  r.use { case (a, b) => IO(println(s"Using $a and $b")) }.unsafeRunSync

  println("-----\n")
}
