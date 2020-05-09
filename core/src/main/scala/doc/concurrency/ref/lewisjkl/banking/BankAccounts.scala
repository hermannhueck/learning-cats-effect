/*
  See: https://lewisjkl.com/cats-effect-ref/
 */
package doc.concurrency.ref.lewisjkl.banking

import cats.effect.concurrent.Ref
import cats.effect.IO

object BankAccounts {
  type Balance = Int
  final case class BankAccount(number: String, balance: Balance)
}

import BankAccounts._

final class BankAccounts(ref: Ref[IO, Map[String, BankAccount]]) {

  def alterAmount(accountNumber: String, amount: Int): IO[Option[Balance]] = {
    ref.modify { allBankAccounts =>
      val maybeBankAccount = allBankAccounts.get(accountNumber).map { bankAccount =>
        bankAccount.copy(balance = bankAccount.balance + amount)
      }
      val newBankAccounts = allBankAccounts ++ maybeBankAccount.map(account => (account.number, account))
      val maybeNewBalance = maybeBankAccount.map(_.balance)
      (newBankAccounts, maybeNewBalance)
    }
  }

  def getBalance(accountNumber: String): IO[Option[Balance]] =
    ref.get.map(_.get(accountNumber).map(_.balance))

  def addAccount(account: BankAccount): IO[Unit] =
    ref.update(_ + (account.number -> account))
}
