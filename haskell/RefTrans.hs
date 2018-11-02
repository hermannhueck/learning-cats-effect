module Main
where

f :: IO a -> IO a -> IO a
f ioaction1 ioaction2 = do
    ioaction1
    ioaction2

main :: IO ()
main = do
  putStrLn "----- Referential Transparency"
  f (putStrLn "hi") (putStrLn "hi")
  putStrLn "-----"
  -- is equivalent to
  let x = putStrLn "hi" in f x x
  putStrLn "-----"