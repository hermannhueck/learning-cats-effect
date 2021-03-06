package doc.datatypes.io

object App02FibonacciWithoutIO extends hutil.App {

  def fib(n: Int, a: Long = 0, b: Long = 1): Long =
    if (n > 0)
      fib(n - 1, b, a + b)
    else
      a + b

  println(fib(5))
}
