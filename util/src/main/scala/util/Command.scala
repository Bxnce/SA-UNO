package util

import de.htwg.se.uno.controller.controllerComponent.controllerInterface
import de.htwg.se.uno.model.gameComponent.gameInterface

trait Command(controller: controllerInterface) { //template Pattern eingebaut
  val oldgame = controller.game
  var newgame = controller.game

  def execute: gameInterface =
    oldgame
  def undoStep: gameInterface =
    oldgame
  def redoStep: gameInterface =
    newgame
}
