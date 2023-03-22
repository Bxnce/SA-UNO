package de.htwg.se.uno
package util

import controller.controllerComponent.controllerInterface
import model.gameComponent.gameInterface

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
