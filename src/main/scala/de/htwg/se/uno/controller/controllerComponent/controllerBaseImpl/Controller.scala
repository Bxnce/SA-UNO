package de.htwg.se.uno
package controller.controllerComponent.controllerBaseImpl

import controller.controllerComponent._

import com.google.inject.name.Names
import com.google.inject.{Guice, Inject}
import scala.io.StdIn.readLine
import model.gameComponent.gameBaseImpl.Game
import model.gameComponent.gameBaseImpl.toCard._
import model.fileIOComponent.FileIOInterface
import util.Observable
import util.Invoker
import model.gameComponent._
import Console.{RED, RESET}
import model.gameComponent.gameBaseImpl.Player
import model.gameComponent.gameBaseImpl.CardValue
import model.gameComponent.gameBaseImpl.CardColor
import scala.collection.mutable.ListBuffer


case class Controller @Inject() (var game: gameInterface)
    extends controllerInterface:
  val invoker = new Invoker

  def take() =
    game = invoker.doStep(UnoCommand(this, "take"))
    notifyObservers

  def place(ind: Int) =
    game = invoker.doStep(UnoCommand(ind, this))
    notifyObservers

  def next() =
    game = invoker.doStep(UnoCommand(this, "next"))
    notifyObservers

  def undo() =
    game = invoker.undoStep.getOrElse(game)
    notifyObservers

  def redo() =
    game = invoker.redoStep.getOrElse(game)
    notifyObservers

  def newG(p1: String, p2: String) =
    game = new Game(p1, p2, between21State).init()
    notifyObservers

  def WinG(p1: String, p2: String) =
    game = new Game(p1, p2, between21State).init()

  def colorChoose(color: String) =
    game = invoker.doStep(UnoCommand(color, this))
    notifyObservers

  def save: Unit =
    def fileIO =
      Guice.createInjector(new UnoModule).getInstance(classOf[FileIOInterface])
    fileIO.save(game)
    notifyObservers



  def load: Unit =
    def fileIO =
      Guice.createInjector(new UnoModule).getInstance(classOf[FileIOInterface])
    game = fileIO.load
    notifyObservers

  override def toString: String =
    UnoCommand(this, "print").toString

  def return_j: String =
    def fileIO =
      Guice.createInjector(new UnoModule).getInstance(classOf[FileIOInterface])
    var ret_str = ""
    if game.pList(0).name != "place_h" then
      ret_str = fileIO.return_json(game)
    ret_str

  def create_tuple() : List[List[(String, Int)]] =
    var card_list = new ListBuffer[List[(String, Int)]]()
    card_list += create_per_player(this.game.pList(0))
    card_list += create_per_player(this.game.midCard)
    card_list += create_per_player(this.game.pList(1))
    card_list.toList



  def create_per_player(player: Player) : List[(String, Int)] =
    var cards = new ListBuffer[(String, Int)]()
    var ind = 0
    for(c <- player.karten){
      var color = ""
      var value = ""
      c.getColor match {
        case CardColor.Red      => color = "red"
        case CardColor.Blue     => color = "blue"
        case CardColor.Green    => color = "green"
        case CardColor.Yellow   => color = "yellow"
        case CardColor.Black    => color = "black"
        case CardColor.ErrorC   => color = ""
      }
      c.getValue match {
        case CardValue.Zero     => value = "_0"
        case CardValue.One      => value = "_1"
        case CardValue.Two      => value = "_2"
        case CardValue.Three    => value = "_3"
        case CardValue.Four     => value = "_4"
        case CardValue.Five     => value = "_5"
        case CardValue.Six      => value = "_6"
        case CardValue.Seven    => value = "_7"
        case CardValue.Eight    => value = "_8"
        case CardValue.Nine     => value = "_9"
        case CardValue.Take2    => value = "_+2"
        case CardValue.Skip     => value = "_skip"
        case CardValue.Wildcard => value = "_wildcard"
        case CardValue.Take4    => value = "_+4"
        case CardValue.Special  => value = ""
        case CardValue.Error    => value = ""
      }
      var card_s = "cards/" + color + value + ".png"
      var tup = (card_s, ind)
      cards += tup
      ind += 1
    }
    cards.toList

