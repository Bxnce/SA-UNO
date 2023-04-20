package controller.controllerComponent

import util.{Invoker, Observable}
import controller.controllerComponent.controllerBaseImpl.*
import model.gameComponent.gameInterface
import model.gameComponent.gameBaseImpl.Player

trait controllerInterface extends Observable {
  var game: gameInterface
  val invoker: Invoker[gameInterface]
  def take(): Unit
  def place(ind: Int): Unit
  def next(): Unit
  def undo(): Unit
  def redo(): Unit
  def newG(p1: String, p2: String): Unit
  def WinG(p1: String, p2: String): Unit
  def colorChoose(color: String): Unit
  override def toString: String
  def load(): Unit
  def save(): Unit
  def create_per_player(player: Player) : List[(String, Int)]
  def create_tuple() : List[List[(String, Int)]]
}
