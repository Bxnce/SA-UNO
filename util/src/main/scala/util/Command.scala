package util

trait Command[T]:
  def execute: T
  def undoStep: T
  def redoStep: T
