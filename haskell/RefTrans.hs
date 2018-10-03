module Main
where

f :: IO a -> IO a -> IO a
f ioaction1 ioaction2 = do
    ioaction1
    ioaction2

main = do
  print "\n----- Referential Transparency"
  f (putStrLn "hi") (putStrLn "hi")
  print "-----"
  -- is equivalent to
  let x = putStrLn "hi" in f x x
  print "-----\n"