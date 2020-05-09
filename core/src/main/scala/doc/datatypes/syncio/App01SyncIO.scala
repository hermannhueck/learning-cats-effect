package doc.datatypes.syncio

import cats.effect.SyncIO

object App01SyncIO extends hutil.App {

  def putStrLn(str: String): SyncIO[Unit] = SyncIO(println(str))
  // putStrLn: (str: String)cats.effect.SyncIO[Unit]

  SyncIO.pure("Cats!").flatMap(putStrLn).unsafeRunSync
  // Cats!
}
