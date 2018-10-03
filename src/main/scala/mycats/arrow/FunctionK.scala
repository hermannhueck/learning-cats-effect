package mycats.arrow

import scala.language.higherKinds

// λ[...] is a polymorphic function value
// (not supported by standard Scala, but provided by kind-projector)
// see: https://github.com/non/kind-projector
//
trait FunctionK[F[_], G[_]] extends Serializable { self =>

  // Applies this functor transformation from `F` to `G`
  def apply[A](fa: F[A]): G[A]

  // Composes two instances of FunctionK into a new FunctionK with this transformation applied last.
  def compose[E[_]](f: FunctionK[E, F]): FunctionK[E, G] =
    λ[FunctionK[E, G]](fa => self(f(fa)))

  // Composes two instances of FunctionK into a new FunctionK with this transformation applied first.
  def andThen[H[_]](f: FunctionK[G, H]): FunctionK[F, H] =
    f.compose(self)
}

object FunctionK {

  // The identity transformation of `F` to `F`
  def id[F[_]]: FunctionK[F, F] = λ[FunctionK[F, F]](fa => fa)
}
