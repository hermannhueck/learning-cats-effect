package mycats.effect

case class IO[A](run: () => A) {

  def map[B](f: A => B): IO[B] = IO { () => f(run()) }

  def flatMap[B](f: A => IO[B]): IO[B] = f(run())
}

object IO {

  //def apply[A](a: => A): IO[A] = IO { () => a }
}
