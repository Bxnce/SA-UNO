package controller.controllerComponent.controllerBaseImpl

import controller.controllerComponent.controllerInterface
import com.google.inject.name.Names
import com.google.inject.{Guice, Inject}
import scala.io.StdIn.readLine
import Console.{RED, RESET}
import scala.collection.mutable.ListBuffer
import model.gameComponent.gameBaseImpl._
import model.gameComponent.gameInterface
import controller.controllerComponent.Invoker
import fileIOComponent.JSONImpl.fileIO

case class Controller @Inject() (var game: gameInterface)
    extends controllerInterface:
  val invoker = new Invoker
  val fileIO = new fileIO

  def take(): Unit =
    game = invoker.doStep(UnoCommand(this.game, "take"))
    print("conTake")
    notifyObservers

  def place(ind: Int): Unit =
    game = invoker.doStep(UnoCommand(ind, this.game))
    print("conPlace")
    notifyObservers

  def next(): Unit =
    game = invoker.doStep(UnoCommand(this.game, "next"))
    print("conNext")
    notifyObservers

  def undo(): Unit =
    game = invoker.undoStep.getOrElse(game)
    print("conUndo")
    notifyObservers

  def redo(): Unit =
    game = invoker.redoStep.getOrElse(game)
    print("conRedo")
    notifyObservers

  def newG(p1: String, p2: String): Unit =
    game = new Game(p1, p2, UnoState.between21State).init()
    print("conNewG")
    notifyObservers

  def WinG(p1: String, p2: String): Unit =
    game = new Game(p1, p2, UnoState.between21State).init()
    print("conWinG")

  def colorChoose(color: String): Unit =
    game = invoker.doStep(UnoCommand(color, this.game))
    print("conColorChoose")
    notifyObservers

  def save(): Unit =
    fileIO.save(game)
    print("conSave")
    notifyObservers

  def load(): Unit =
    fileIO.load
    print("conLoad")
    notifyObservers

  override def toString: String =
    UnoCommand(this.game, "print").toString


  def create_tuple() : List[List[(String, Int)]] =
    val card_list = new ListBuffer[List[(String, Int)]]()
    card_list += create_per_player(this.game.pList.head)
    card_list += create_per_player(this.game.midCard)
    card_list += create_per_player(this.game.pList(1))
    card_list.toList


  def create_per_player(player: Player): List[(String, Int)] =
    player.karten.zipWithIndex.map { (c, ind) =>
      val color = c.color match {
        case CardColor.Red => "red"
        case CardColor.Blue => "blue"
        case CardColor.Green => "green"
        case CardColor.Yellow => "yellow"
        case CardColor.Black => "black"
        case CardColor.ErrorC => ""
      }
      val value = c.value match {
        case CardValue.Zero => "_0"
        case CardValue.One => "_1"
        case CardValue.Two => "_2"
        case CardValue.Three => "_3"
        case CardValue.Four => "_4"
        case CardValue.Five => "_5"
        case CardValue.Six => "_6"
        case CardValue.Seven => "_7"
        case CardValue.Eight => "_8"
        case CardValue.Nine => "_9"
        case CardValue.Take2 => "_+2"
        case CardValue.Skip => "_skip"
        case CardValue.Wildcard => "_wildcard"
        case CardValue.Take4 => "_+4"
        case CardValue.Special | CardValue.Error => ""
      }
      val card_s = s"cards/${color}${value}.png"
      (card_s, ind)
    }.toList



