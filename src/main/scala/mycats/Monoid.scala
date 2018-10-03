package mycats

trait Monoid[A] {

  def empty: A
  def combine(x: A, y: A): A

  def combineAll(as: List[A]): A = // combines all functions in a List of functions
    as.foldLeft(empty)(combine)
}

object Monoid {

  object syntax {

    implicit class MonoidSyntax[A: Monoid](x: A) {

      def combine(y: A): A = Monoid[A].combine(x, y)
      def |+|(y: A): A = x combine y
    }
  }

  def apply[A: Monoid]: Monoid[A] = implicitly

  // default typeclass instances in implicit scope

  implicit val stringMonoid: Monoid[String] = new Monoid[String] {
    override def empty: String = ""
    override def combine(x: String, y: String): String = x + y
  }

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override def empty: Int = 0
    override def combine(x: Int, y: Int): Int = x + y
  }

  implicit def listMonoid[A]: Monoid[List[A]] = new Monoid[List[A]] {
    override def empty: List[A] = List.empty[A]
    override def combine(x: List[A], y: List[A]): List[A] = x ++ y
  }

  // This one is the default Function1-Monoid in Cats
  implicit def function1Monoid[A, B: Monoid]: Monoid[A => B] = new Monoid[A => B] {
    override def empty: A => B = _ => Monoid[B].empty
    override def combine(f: A => B, g: A => B): A => B = a => Monoid[B].combine(f(a), g(a))
  }
}
