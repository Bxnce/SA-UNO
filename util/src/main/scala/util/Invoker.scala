package util

import model.gameComponent.gameInterface
import util.Command

class Invoker {
  private var undoStack: List[Command[gameInterface]] = Nil
  private var redoStack: List[Command[gameInterface]] = Nil

  def doStep(command: Command[gameInterface]): gameInterface =
    command match
      case _ =>
        undoStack = command :: undoStack
        redoStack = Nil
    command.execute

  def undoStep: Option[gameInterface] = {
    undoStack match {
      case Nil => None
      case head :: stack => {
        undoStack = stack
        redoStack = head :: redoStack
        Some(head.undoStep)
      }
    }
  }

  def redoStep: Option[gameInterface] = {
    redoStack match {
      case Nil => None
      case head :: stack => {
        redoStack = stack
        undoStack = head :: undoStack
        Some(head.redoStep)
      }
    }
  }
}
