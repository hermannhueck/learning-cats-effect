package doc.typeclasses.concurrenteffect

import cats.effect.{Concurrent, ConcurrentEffect, IO}

class App01ConcurrentEffect {

  def convert[F[_], G[_], A](fa: F[A])(implicit F: ConcurrentEffect[F], G: Concurrent[G]): G[A] =
    G.cancelable { cb =>
      val token = F.runCancelable(fa)(r => IO(cb(r))).unsafeRunSync()
      convert[F, G, Unit](token)
    }
}
