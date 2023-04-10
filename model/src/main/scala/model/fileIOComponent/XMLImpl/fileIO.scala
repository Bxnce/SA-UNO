package model.fileIOComponent.XMLImpl

import model.fileIOComponent.FileIOInterface

import java.io.*

import scala.collection.mutable.ListBuffer
import scala.xml.{NodeSeq, PrettyPrinter}

class fileIO extends FileIOInterface {

  override def load: gameInterface = {
    val file = scala.xml.XML.loadFile("game.xml")
    val player1 = (file \\ "game" \ "player1")
    val p1N = (player1 \\ "name").text
    val p1V = (player1 \\ "karten").text
    val V1 = toCardVector(p1V)
    val p1P = false
    (player1 \\ "placed").text match
      case "true" => true
      case "false" => false
    val player2 = (file \\ "game" \ "player2")
    val p2N = (player2 \\ "name").text
    val p2V = (player2 \\ "karten").text
    val V2 = toCardVector(p2V)
    val p2P = false
    (player2 \\ "placed").text match
      case "true" => true
      case "false" => false
    val currentstate: State = (file \\ "game" \ "currentstate").text match
          case "between12State" => between12State
          case "between21State" => between21State
          case "player1State" => player1State
          case "player2State" => player2State
          case "winState" => winState
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
      <name>
        {player.name}
      </name>
      <karten>
        {player.karten.map(_.toString).map("" + _ + " ")}
      </karten>
      <placed>
        {player.placed.toString}
      </placed>
    </player>

  def gametoXml(game: gameInterface) =
    <game>
      <player1>
        {playertoXml(game.pList(0))}
      </player1>
      <player2>
        {playertoXml(game.pList(1))}
      </player2>
      <currentstate>
        {game.currentstate.toString}
      </currentstate>
      <error>
        {game.ERROR}
      </error>
      <cardStack>
        {cardStacktoXML(game.cardStack.cards)}
      </cardStack>
      <midCard>
        {playertoXml(game.midCard)}
      </midCard>
      <winner>
        {game.winner}
      </winner>
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
    text.split(" ").map(getCard).toVector

  def cardStacktoXML(stack: Map[Card, Int]): Seq[NodeSeq] =
    stack.map { case (card, count) =>
      cardStackString(card, count)
    }.toSeq

  def cardStackString(c: Card, value: Int) = {
    <pair>
      <card>
        {c.toString}
      </card>
      <value>
        {value.toString}
      </value>
    </pair>
  }

  def return_json(game: gameInterface): String = {
    val xml = gametoXml(game).toString
    xml
  }

  def toStackMap(mapString: NodeSeq): Map[Card, Int] =
    mapString.map { temp =>
      val card = getCard((temp \\ "card").text)
      val value = (temp \\ "value").text.toInt
      (card, value)
    }.toMap

}
