package doc.datatypes.io

import java.io._

import scala.concurrent.ExecutionContext

object App11aResource extends App {

  println("\n-----")

  def javaReadFirstLine(file: File): String = {
    val in = new BufferedReader(new FileReader(file))
    try {
      in.readLine()
    } finally {
      in.close()
    }
  }

  implicit val ec: ExecutionContext = ExecutionContext.global

  val line = javaReadFirstLine(new File("README.md"))

  println(line)

  println("-----\n")
}
