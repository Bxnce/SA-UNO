package controller.controllerComponent.controllerBaseImpl

//import controller.controllerComponent

import util.Command
import controller.controllerComponent.controllerInterface
import model.gameComponent.gameBaseImpl._
import model.gameComponent._
import model.gameComponent.gameBaseImpl.CardLayout.eol

case class TakeCommand(game : gameInterface) extends Command[gameInterface]:
  val oldgame = game
  var newgame = game

  override def execute =
    newgame.currentstate match
      case UnoState.player1State =>
        newgame = newgame.take("P1")
        newgame = newgame.setError(0)
      case UnoState.player2State =>
        newgame = newgame.take("P2")
        newgame = newgame.setError(0)
      case UnoState.between12State =>
        newgame = newgame.setError(-1)
      case UnoState.between21State =>
        newgame = newgame.setError(-1)
      case UnoState.winState =>
        newgame = newgame.setError(-1)
    newgame

  override def undoStep: gameInterface =
    oldgame

  override def redoStep: gameInterface =
    newgame


case class PlaceCommand(ind: Int, game: gameInterface) extends Command[gameInterface]:
  val oldgame = game
  var newgame = game

  override def execute =
    newgame.currentstate match
      case UnoState.player1State =>
        newgame = newgame.place(ind, 0)
        if (newgame.ERROR != -1) {
          if (newgame.checkWin(newgame.pList(0))) {
            newgame = newgame.getNext(newgame, 0, UnoState.winState)
          } else {
            newgame = newgame.getNext(newgame, 0, UnoState.between12State)
          }
        }
      case UnoState.player2State =>
        newgame = newgame.place(ind, 1)
        if (newgame.ERROR != -1) {
          if (newgame.checkWin(newgame.pList(1))) {
            newgame = newgame.getNext(newgame, 1, UnoState.winState)
          } else {
            newgame = newgame.getNext(newgame, 1, UnoState.between21State)
          }
        }
      case UnoState.between12State =>
        newgame = newgame.setError(-1)
      case UnoState.between21State =>
        newgame = newgame.setError(-1)
      case UnoState.winState =>
        newgame = newgame.setError(-1)
    newgame

  override def undoStep: gameInterface =
    oldgame

  override def redoStep: gameInterface =
    newgame

case class NextCommand(game: gameInterface) extends Command[gameInterface] {
  val oldgame = game
  var newgame = game

  override def execute =
    newgame.currentstate match
      case UnoState.player1State =>
        newgame = newgame.getNext(newgame, 0, UnoState.between12State)
      case UnoState.player2State =>
        newgame = newgame.getNext(newgame, 0, UnoState.between21State)
      case UnoState.between12State =>
        newgame = newgame.getNext(newgame, -1, UnoState.player2State)
      case UnoState.between21State =>
        newgame = newgame.getNext(newgame, -1, UnoState.player1State)
      case UnoState.winState =>
        newgame = new Game(newgame.pList.head.name, newgame.pList(1).name, UnoState.between21State).init()
    newgame

  override def undoStep: gameInterface =
    oldgame

  override def redoStep: gameInterface =
    newgame
}

case class toStringCommand(game: gameInterface) extends Command[gameInterface] {

  override def execute =
    game
  override def toString: String =
    if (!game.pList(0).name.equals("place_h")) {
      if (game.currentstate == UnoState.player1State) {
        return game.pList(0).name + eol + game
          .pList(0)
          .print() + eol + game.midCard
          .print() + eol + game
          .pList(1)
          .printFiller() + game.pList(1).name + eol
      } else if (game.currentstate == UnoState.player2State) {
        return game.pList(0).name + eol + game
          .pList(0)
          .printFiller() + eol + game.midCard
          .print() + eol + game
          .pList(1)
          .print() + game.pList(1).name + eol
      } else if (game.currentstate == UnoState.winState) {
        return game
          .pList(
            game.winner
          )
          .name
          + " hat gewonnen! 'next' fuer neues Spiel!"
      } else {
        return game
          .pList(0)
          .name + eol + game
          .pList(0)
          .printFiller() + eol + game.midCard
          .print() + eol + game
          .pList(1)
          .printFiller() + game.pList(1).name + eol
      }
    } else {
      return ""
    }

  override def undoStep: gameInterface =
    print("undo")
    game

  override def redoStep: gameInterface =
    print("redo")
    game
}

case class colorChooseCommand(color: String, game: gameInterface)
  extends Command[gameInterface] {
  val oldgame = game
  var newgame = game
  override def execute =
    newgame = newgame.chooseColor(color)
    newgame
  override def undoStep: gameInterface =
    oldgame

  override def redoStep: gameInterface =
    newgame
}

object UnoCommand { //Factory
  def apply(game: gameInterface, dec: String) =
    dec match
      case "take" =>
        new TakeCommand(game)
      case "next" =>
        new NextCommand(game)
      case "print" =>
        new toStringCommand(game)

  def apply(ind: Int, game: gameInterface) =
    new PlaceCommand(ind, game)

  def apply(color: String, game: gameInterface) =
    new colorChooseCommand(color, game)
}