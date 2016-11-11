module TwoDebugger.Stack where

{-
  Implements a stack data structure, endowed with some integer math capabilities.
-}

import Data.Char (ord, chr, isAscii)
import Text.Read (readMaybe) -- to ask for integers

type Stack = [Int]

push :: Int -> Stack -> Stack
push = (:)

peek :: Stack -> Int
peek []     = 0
peek (x:s)  = x

pop :: Stack -> (Int, Stack)
pop []    = (0, [])
pop (x:s) = (x,  s)


binary :: (Int -> Int -> Int) -> Stack -> Stack
binary f s =
  let (a, rest') = pop s
      (b, rest ) = pop rest'
  in  push (f b a) rest

add :: Stack -> Stack
add = binary (+)

subtract :: Stack -> Stack
subtract = binary (-)

multiply :: Stack -> Stack
multiply = binary (*)

divide :: Stack -> Stack
divide = binary quot

modulo :: Stack -> Stack
modulo = binary mod

class Unitable a where
  unitize :: a -> Int

instance Unitable Int where
  unitize 0 = 0
  unitize _ = 1

instance Unitable Bool where
  unitize False = 0
  unitize True  = 1

not :: Stack -> Stack
not s =
  let (x, rest) = pop s
  in  push (1 - unitize x) rest

greater :: Stack -> Stack
greater s =
  let
    (a, rest') = pop s
    (b, rest ) = pop s
  in push (unitize $ b > a) s

duplicate :: Stack -> Stack
duplicate s = push (peek s) s

swap :: Stack -> Stack
swap s =
  let
    (a, rest') = pop s
    (b, rest ) = pop rest'
  in push b $ push a rest

-- uh oh, we're probably going to have to taint the entire stack with IO in order to get input...
{-
class ValidatedInput a where
  validateInput :: a -> Bool

instance ValidatedInput Char where
  validateInput = isAscii

instance ValidatedInput Int where
  validateInput = validateInput . chr -- heh

instance ValidatedInput (Maybe a) where
  validateInput (Just x)  = validateInput x
  validateInput Nothing   = False

keepAsking :: String -> Maybe a -> a
keepAsking prompt input
  | validateInput input = input
  | otherwise           = keepAsking prompt (fmap readMaybe (putStr prompt >> getLine))

askForValue :: String -> a
askForValue = flip keepAsking Nothing

askInt :: Stack -> Stack
askInt = push (askForValue "Please give me an int in [0, 128): ")

askChar :: Stack -> Stack
askChar = push (ord $ askForValue "Please give me an ascii character: ")
-}
