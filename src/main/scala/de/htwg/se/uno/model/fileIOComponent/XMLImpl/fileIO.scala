package de.htwg.se.uno
package model.fileIOComponent.XMLImpl

import controller.controllerComponent.controllerBaseImpl.{
  player1State,
  player2State,
  between12State,
  between21State,
  winState
}
import model.gameComponent.gameInterface
import model.fileIOComponent.FileIOInterface
import java.io._
import controller.controllerComponent.controllerBaseImpl.between21State
import model.gameComponent.gameBaseImpl.toCard._
import model.gameComponent.gameBaseImpl.{CardStack, Card, Game, Player}
import util.State
import scala.collection.mutable.ListBuffer

import scala.xml.{NodeSeq, PrettyPrinter}

class fileIO extends FileIOInterface {

  override def load: gameInterface = {
    val file = scala.xml.XML.loadFile("game.xml")
    val player1 = (file \\ "game" \ "player1")
    val p1N = (player1 \\ "name").text
    val p1V = (player1 \\ "karten").text
    val V1 = toCardVector(p1V)
    var p1P = false
    (player1 \\ "placed").text match
      case "true"  => p1P = true
      case "false" => p1P = false
    val player2 = (file \\ "game" \ "player2")
    val p2N = (player2 \\ "name").text
    val p2V = (player2 \\ "karten").text
    val V2 = toCardVector(p2V)
    var p2P = false
    (player2 \\ "placed").text match
      case "true"  => p2P = true
      case "false" => p2P = false
    var currentstate: State = between21State
    (file \\ "game" \ "currentstate").text match
      case "between12State" => currentstate = between12State
      case "between21State" => currentstate = between21State
      case "player1State"   => currentstate = player1State
      case "player2State"   => currentstate = player2State
      case "winState"       => currentstate = winState
    val ERROR = (file \\ "game" \ "error").text.toInt
    val stackNote = (file \\ "pair")
    val cardStack: CardStack = new CardStack(
      toStackMap(stackNote)
    )
    val midCard = (file \\ "game" \ "midCard")
    val pmN = (midCard \\ "name").text
    val pmV = (midCard \\ "karten").text
    val VM = toCardVector(pmV)
    val winner = (file \\ "game" \ "winner").text.toInt

    val p1 = new Player(p1N, V1, p1P)
    val p2 = new Player(p2N, V2, p2P)
    val mCard = new Player(pmN, VM, false)
    val game =
      new Game(List(p1, p2), currentstate, ERROR, cardStack, mCard, winner)
    game
  }

  def playertoXml(player: Player) =
    <player> 
        <name>{player.name}</name> 
        <karten>{player.karten.map(_.toString).map("" + _ + " ")}</karten>
        <placed>{player.placed.toString}</placed>
    </player>

  def gametoXml(game: gameInterface) =
    <game>
            <player1>{playertoXml(game.pList(0))}</player1>
            <player2>{playertoXml(game.pList(1))}</player2>
            <currentstate>{game.currentstate.toString}</currentstate>
            <error>{game.ERROR}</error>
            <cardStack>{cardStacktoXML(game.cardStack.cards)}</cardStack>
            <midCard>{playertoXml(game.midCard)}</midCard>
            <winner>{game.winner}</winner>
    </game>

  override def save(game: gameInterface): Unit =
    saveString(game)

  def saveString(game: gameInterface): Unit =
    val pw = new PrintWriter(new File("game.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(gametoXml(game))
    pw.write(xml)
    pw.close

  def toCardVector(text: String): Vector[Card] =
    val tmp = text.split(" ")
    var cardstmp = new ListBuffer[Card]()
    for (i <- tmp) {
      cardstmp += getCard(i)
    }
    cardstmp.toVector

  def cardStacktoXML(stack: Map[Card, Int]) = {
    {
      for (i <- stack) yield cardStackString(i(0), i(1))
    }
  }

  def cardStackString(c: Card, value: Int) = {
    <pair>
        <card>{c.toString}</card>
        <value>{value.toString}</value>
    </pair>
  }

  def return_json(game: gameInterface): String = {
    val xml = gametoXml(game).toString
    xml
  }
  def toStackMap(mapString: NodeSeq): Map[Card, Int] =
    var sMap: Map[Card, Int] = Map[Card, Int]()
    for (temp <- mapString) {
      sMap = sMap.updated(
        getCard((temp \\ "card").text),
        (temp \\ "value").text.toInt
      )
    }
    sMap

}
