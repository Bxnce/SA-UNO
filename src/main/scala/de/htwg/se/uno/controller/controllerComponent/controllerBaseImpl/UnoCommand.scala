package de.htwg.se.uno
package controller.controllerComponent.controllerBaseImpl

//import controller.controllerComponent

import util.Command
import controller.controllerComponent.controllerInterface
import model.gameComponent.gameBaseImpl._
import model.gameComponent._
import model.gameComponent.gameBaseImpl.CardLayout.eol

case class TakeCommand(controller: controllerInterface)
    extends Command(controller) {
  override def execute =
    newgame = controller.game.currentstate.handle(this)
    newgame
}

case class PlaceCommand(ind: Int, controller: controllerInterface)
    extends Command(controller) {

  override def execute =
    newgame = controller.game.currentstate.handle(this)
    newgame
}

case class NextCommand(controller: controllerInterface)
    extends Command(controller) {

  override def execute =
    newgame = controller.game.currentstate.handle(this)
    newgame
}

case class toStringCommand(controller: controllerInterface)
    extends Command(controller) {

  override def execute =
    controller.game
  override def toString: String =
    if (!controller.game.pList(0).name.equals("place_h")) {
      if (controller.game.currentstate == player1State) {
        return controller.game.pList(0).name + eol + controller.game
          .pList(0)
          .print() + eol + controller.game.midCard
          .print() + eol + controller.game
          .pList(1)
          .printFiller() + controller.game.pList(1).name + eol
      } else if (controller.game.currentstate == player2State) {
        return controller.game.pList(0).name + eol + controller.game
          .pList(0)
          .printFiller() + eol + controller.game.midCard
          .print() + eol + controller.game
          .pList(1)
          .print() + controller.game.pList(1).name + eol
      } else if (controller.game.currentstate == winState) {
        return controller.game
          .pList(
            controller.game.winner
          )
          .name
          + " hat gewonnen! 'next' fuer neues Spiel!"
      } else {
        return controller.game
          .pList(0)
          .name + eol + controller.game
          .pList(0)
          .printFiller() + eol + controller.game.midCard
          .print() + eol + controller.game
          .pList(1)
          .printFiller() + controller.game.pList(1).name + eol
      }
    } else {
      return ""
    }
}

case class colorChooseCommand(color: String, controller: controllerInterface)
    extends Command(controller) {
  override def execute =
    controller.game.chooseColor(color)

}

object UnoCommand { //Factory
  def apply(controller: controllerInterface, dec: String) =
    dec match
      case "take" =>
        new TakeCommand(controller)
      case "next" =>
        new NextCommand(controller)
      case "print" =>
        new toStringCommand(controller)

  def apply(ind: Int, controller: controllerInterface) =
    new PlaceCommand(ind, controller)

  def apply(color: String, controller: controllerInterface) =
    new colorChooseCommand(color, controller)
}
