package util

import util.Command

class Invoker[T] {
  private var undoStack: List[Command[T]] = Nil
  private var redoStack: List[Command[T]] = Nil

  def doStep(command: Command[T]): T =
    command match
      case _ =>
        undoStack = command :: undoStack
        redoStack = Nil
    command.execute

  def undoStep: Option[T] = {
    undoStack match {
      case Nil => None
      case head :: stack =>
        undoStack = stack
        redoStack = head :: redoStack
        Some(head.undoStep)
    }
  }

  def redoStep: Option[T] = {
    redoStack match {
      case Nil => None
      case head :: stack =>
        redoStack = stack
        undoStack = head :: undoStack
        Some(head.redoStep)
    }
  }
}
