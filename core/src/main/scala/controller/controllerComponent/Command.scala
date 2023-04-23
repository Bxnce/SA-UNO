package controller.controllerComponent

trait Command[T]:
  def execute: T
  def undoStep: T
  def redoStep: T
