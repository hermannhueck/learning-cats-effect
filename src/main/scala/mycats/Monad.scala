package mycats

import scala.concurrent.Future
import scala.language.higherKinds

trait Monad[F[_]] extends Functor[F] {

  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  override def map[A, B](fa: F[A])(f: A => B): F[B] = flatMap(fa)(a => pure(f(a)))
  def flatten[A](ffa: F[F[A]]): F[A] = flatMap(ffa)(identity)
}

object Monad {

  object syntax {

    implicit class MonadSyntax[F[_]: Monad, A](fa: F[A]) {
      def flatMap[B](f: A => F[B]): F[B] = Monad[F].flatMap(fa)(f)
    }

    /*
    implicit class MonadSyntaxFunction1[P, A](f: Function1[P, A]) {
      def flatMap[B](g: A => P => B): P => B = Monad[Function1[P, ?]].flatMap(f)(g)
    }
    */
  }

  def apply[F[_]: Monad]: Monad[F] = implicitly

  // Kleisli composition
  def kleisliCompose[F[_]: Monad, A, B, C](f: A => F[B], g: B => F[C]): A => F[C] =
    a => Monad[F].flatMap(f(a))(g)

  // default typeclass instances in implicit scope

  implicit def listMonad: Monad[List] = new Monad[List] {
    override def pure[A](a: A): List[A] = List(a)
    override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] = fa flatMap f
  }

  implicit def optionMonad: Monad[Option] = new Monad[Option] {
    override def pure[A](a: A): Option[A] = Option(a)
    override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa flatMap f
  }

  implicit def vectorMonad: Monad[Vector] = new Monad[Vector] {
    override def pure[A](a: A): Vector[A] = Vector(a)
    override def flatMap[A, B](fa: Vector[A])(f: A => Vector[B]): Vector[B] = fa flatMap f
  }

  implicit def futureMonad: Monad[Future] = new Monad[Future] {
    import scala.concurrent.ExecutionContext.Implicits.global
    override def pure[A](a: A): Future[A] = Future(a)
    override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa flatMap f
  }

  implicit def idMonad: Monad[Id] = new Monad[Id] {
    override def pure[A](a: A): Id[A] = a
    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)
  }

  implicit def eitherMonad[L]: Monad[Either[L, ?]] = new Monad[Either[L, ?]] {
    override def pure[A](r: A): Either[L, A] = Right(r)
    override def flatMap[A, B](fa: Either[L, A])(f: A => Either[L, B]): Either[L, B] = fa flatMap f
  }

  implicit def tuple2Monad[L: Monoid]: Monad[Tuple2[L, ?]] = new Monad[Tuple2[L, ?]] {
    override def pure[R](r: R): (L, R) = (Monoid[L].empty, r)
    override def flatMap[R1, R2](fa: (L, R1))(f: R1 => (L, R2)): (L, R2) = fa match {
      case (l1, r1) =>
        val (l2, r2) = f(r1)
        val lCombined = Monoid[L].combine(l1, l2)
        (lCombined, r2)
    }
  }

  implicit def function1Monad[P]: Monad[P => ?] = new Monad[P => ?] {
    override def pure[A](r: A): P => A = _ => r
    override def flatMap[A, B](f: P => A)(g: A => P => B): P => B =
      p => g(f(p))(p)
  }

  /*
  implicit def function1Monad[P]: Monad[Function1[P, ?]] = new Monad[Function1[P, ?]] {
    override def pure[A](r: A): Function1[P, A] = _ => r
    override def flatMap[A, B](f: Function1[P, A])(g: A => Function1[P, B]): Function1[P, B] =
      p => g(f(p))(p)
  }
  */
}
