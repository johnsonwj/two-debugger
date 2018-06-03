module TwoDebugger.Befunge93 where

import           Data.Char
import qualified Data.Map.Strict as M
import           Data.Map.Strict (!?)

data Direction = N | W | S | E

data Instruction
  = NoOp
  | Push Int
  | Duplicate
  | Swap
  | Pop
  | Add
  | Subtract
  | Multiply
  | Divide
  | Negate
  | Compare
  | ChangeDirection Direction
  | RandomizeDirection
  | ChooseDirectionH
  | ChooseDirectionV
  | ToggleStringMode
  | PrintInteger
  | PrintChar
  | InputInteger
  | InputChar
  | SkipNext
  | Put
  | Get
  | End

data InstructionPointer = InstructionPointer
  { ipCoordinate  :: Coordinate
  , ipDirection   :: Direction
  }

type Coordinate = (Int, Int)

type Playfield = M.Map Coordinate Instruction

type Stack = [Int]

data Session = Session
  { ip          :: InstructionPointer
  , field       :: Playfield
  , stack       :: Stack
  , stringMode  :: Bool
  }

getInstruction :: Playfield -> Coordinate -> Instruction
getInstruction f c = case (f !? c) of
  Just i  -> i
  Nothing -> NoOp



step :: Session -> Either String Session
step s
  | checkPointerOutOfBounds s = Left "Pointer is out of bounds"
  | otherwise = case (checkInvalidInstruction s) of
    Just e  -> Left e
    Nothing -> Right (doStep s)

checkPointerOutOfBounds :: Session -> Maybe String


-- | 'Nothing' if the current instruction can be executed without error; `Just emsg` with error message
checkInvalidInstruction :: Session -> Maybe String
checkInvalidInstruction _session@(Session (InstructionPointer _coord _dir) _field _stack _stringMode) =

doStep :: Session -> Session
doStep _session@(Session (InstructionPointer _coord _dir) _field _stack _stringMode) =
