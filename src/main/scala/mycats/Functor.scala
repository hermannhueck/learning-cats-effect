package mycats

import scala.concurrent.Future
import scala.language.higherKinds

// typeclass functor
trait Functor[F[_]] {

  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {

  object syntax {

    implicit class FunctorSyntax[F[_]: Functor, A](fa: F[A]) {
      def map[B](f: A => B): F[B] = Functor[F].map(fa)(f)
    }

    /*
    // Functor syntax specific for Function1
    implicit class FunctorSyntaxFunction1[P, A](fa: Function1[P, A]) {
      def map[B](f: A => B): Function1[P, B] = Functor[Function1[P, ?]].map(fa)(f)
    }
    */
  }

  def apply[F[_]: Functor]: Functor[F] = implicitly


  // default typeclass instances in implicit scope

  implicit val listFunctor: Functor[List] = new Functor[List] {
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa map f
  }

  implicit val optionFunctor: Functor[Option] = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa map f
  }

  implicit val futureFunctor: Functor[Future] = new Functor[Future] {
    import scala.concurrent.ExecutionContext.Implicits.global
    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa map f
  }

  implicit val idFunctor: Functor[Id] = new Functor[Id] {
    override def map[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)
  }

  implicit val vectorFunctor: Functor[Vector] = new Functor[Vector] {
    override def map[A, B](fa: Vector[A])(f: A => B): Vector[B] = fa map f
  }

  implicit def eitherFunctor[L]: Functor[Either[L, ?]] = new Functor[Either[L, ?]] {
    override def map[A, B](fa: Either[L, A])(f: A => B): Either[L, B] = fa map f
  }

  implicit def tuple2Functor[L]: Functor[Tuple2[L, ?]] = new Functor[Tuple2[L, ?]] {
    override def map[R1, R2](fa: Tuple2[L, R1])(f: R1 => R2): Tuple2[L, R2] = fa match {
      case(x, y) => (x, f(y))
    }
  }

  implicit def function1Functor[P]: Functor[Function1[P, ?]] = new Functor[Function1[P, ?]] {
    override def map[A, B](f: Function1[P, A])(g: A => B): Function1[P, B] = f andThen g
  }
}
