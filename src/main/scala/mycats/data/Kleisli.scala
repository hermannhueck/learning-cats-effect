package mycats.data

import mycats.{Functor, Monad, ~>}

import scala.language.higherKinds

case class Kleisli[F[_], A, B](run: A => F[B]) {

  def flatMap[C](f: B => Kleisli[F, A, C])(implicit M: Monad[F]): Kleisli[F, A, C] = Kleisli { a =>
    M.flatMap(run(a))(b => f(b).run(a))
  }

  def flatMapF[C](f: B => F[C])(implicit M: Monad[F]): Kleisli[F, A, C] = Kleisli { a =>
    M.flatMap(run(a))(f)
  }

  def andThen[C](f: B => F[C])(implicit M: Monad[F]): Kleisli[F, A, C] =
    flatMapF(f)

  def andThen[C](that: Kleisli[F, B, C])(implicit M: Monad[F]): Kleisli[F, A, C] =
    this andThen that.run

  def compose[Z](f: Z => F[A])(implicit M: Monad[F]): Kleisli[F, Z, B] =
    Kleisli(f) andThen this.run

  def compose[Z](that: Kleisli[F, Z, A])(implicit M: Monad[F]): Kleisli[F, Z, B] =
    that andThen this

  def map[C](f: B => C)(implicit F: Functor[F]): Kleisli[F, A, C] = Kleisli { a =>
    F.map(run(a))(f)
  }

  def mapF[G[_], C](f: F[B] => G[C]): Kleisli[G, A, C] = Kleisli {
    run andThen f       // same as: a => f(run(a))
  }

  def mapK[G[_]](nt: F ~> G): Kleisli[G, A, B] = Kleisli {
    run andThen nt.apply // same as: a => f(run(a))
  }

  def apply(a: A): F[B] = run(a)
}

object Kleisli {

  def pure[F[_], A, B](b: B)(implicit M: Monad[F]): Kleisli[F, A, B] =
    Kleisli { _ => M.pure(b) }

  def ask[F[_], A](implicit M: Monad[F]): Kleisli[F, A, A] =
    Kleisli { a => M.pure(a) }


  // Kleisli Monad instance defined in companion object is in
  // 'implicit scope' (i.e. found by the compiler without import).

  implicit def kleisliMonad[F[_], A](implicit M: Monad[F]): Monad[Kleisli[F, A, ?]] = new Monad[Kleisli[F, A, ?]] {

    override def pure[B](b: B): Kleisli[F, A, B] =
      Kleisli { _ => M.pure(b) }

    override def flatMap[B, C](kl: Kleisli[F, A, B])(f: B => Kleisli[F, A, C]): Kleisli[F, A, C] =
      kl flatMap f
  }
}
