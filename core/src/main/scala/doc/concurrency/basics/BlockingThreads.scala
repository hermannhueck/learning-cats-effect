package doc.concurrency.basics

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{ContextShift, IO}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object BlockingThreads extends hutil.App {

  implicit val contextShift: ContextShift[IO]  = IO.contextShift(ExecutionContext.global)
  private val executorService: ExecutorService = Executors.newCachedThreadPool()
  val blockingEC: ExecutionContextExecutor     = ExecutionContext.fromExecutor(executorService)

  def blockingOp: IO[Unit] = IO(println(s"blocking op (executing on blockingEC): ${Thread.currentThread.getName}"))
  def doSth(): IO[Unit]    = IO(println(s"do something(executing on main thread pool): ${Thread.currentThread.getName}"))

  val prog =
    for {
      _ <- contextShift.evalOn(blockingEC)(blockingOp) // executes on blockingEC
      _ <- doSth()                                     // executes on contextShift
    } yield ()

  prog.unsafeRunSync()

  executorService.shutdown()
}
