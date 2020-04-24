/*
  See: https://lewisjkl.com/cats-effect-ref/
 */
package doc.concurrency.ref.lewisjkl.banking

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import cats.effect.concurrent.Ref

import BankAccounts._

object Example extends IOApp {

  val example = for {
    ref           <- Ref[IO].of(Map.empty[String, BankAccount])
    bankAccounts  = new BankAccounts(ref)
    _             <- bankAccounts.addAccount(BankAccount("1", 0))
    _             <- bankAccounts.alterAmount("1", 50)
    _             <- bankAccounts.alterAmount("1", -25)
    endingBalance <- bankAccounts.getBalance("1")
  } yield endingBalance

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      _             <- IO(println("\u2500" * 50))
      endingBalance <- example
      _             <- IO(println(s"endingBalance = $endingBalance"))
      _             <- IO(println("\u2500" * 50))
    } yield ()).as(ExitCode.Success)
}
