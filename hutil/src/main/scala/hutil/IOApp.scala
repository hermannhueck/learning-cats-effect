package hutil

import java.lang.System.{currentTimeMillis => currentTime}

import hutil.classname._
import hutil.stringformat._

import cats.effect.{ExitCode, IO}
import cats.effect.Resource

trait IOApp extends cats.effect.IOApp {

  val acquire: IO[Long] = {
    IO {
      val executionStart: Long = currentTime
      runtimeInfo.blue.boxed.println
      objectName(this).blue.boxed.println
      executionStart
    }
  }

  val release: Long => IO[Unit] = executionStart => {
    IO {
      val total = currentTime - executionStart
      s"[total: $total ms]".blue.boxed.println
    }
  }
  val res = Resource.make(acquire)(release)

  override def run(args: List[String]): IO[ExitCode] =
    Resource.make(acquire)(release).use(_ => ioRun(args))

  def ioRun(args: List[String]): IO[ExitCode]
}
