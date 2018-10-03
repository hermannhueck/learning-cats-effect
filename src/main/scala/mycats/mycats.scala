import scala.language.higherKinds

package object mycats {

  type Id[A] = A

  type ~>[F[_], G[_]] = mycats.arrow.FunctionK[F, G]
}
