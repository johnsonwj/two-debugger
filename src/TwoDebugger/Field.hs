module TwoDebugger.Field where

type Instruction = Char

class Field a where
  init :: [[Instruction]] -> a

  doInstruction :: a -> a
  movePointer	  :: a -> a

  step          ::  a -> a
  step = movePointer . doInstruction