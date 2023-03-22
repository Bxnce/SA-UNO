import de.htwg.se.uno.controller.controllerComponent.controllerBaseImpl.player1State
import de.htwg.se.uno.controller.controllerComponent.controllerBaseImpl.between21State
import de.htwg.se.uno.controller.controllerComponent.controllerBaseImpl.player2State
import de.htwg.se.uno.controller.controllerComponent.controllerBaseImpl.between12State
import de.htwg.se.uno.controller.controllerComponent.controllerBaseImpl.winState
import de.htwg.se.uno.model.gameComponent.gameInterface
import de.htwg.se.uno.model.gameComponent.gameBaseImpl._
import de.htwg.se.uno.model.gameComponent.gameBaseImpl.Player
import de.htwg.se.uno.model.gameComponent.gameBaseImpl.Card
import de.htwg.se.uno.model.gameComponent.gameBaseImpl.Card._
import de.htwg.se.uno.model.gameComponent.gameBaseImpl.toCard._
import scala.collection.mutable.ListBuffer
import de.htwg.se.uno.util.State

import scala.xml.{NodeSeq, PrettyPrinter}
def gametoXml(game: gameInterface) =
  val s = gameString(game)
  <game>{s}</game>

def gameString(game: gameInterface): String =
  "player1={" + playertoXml(game.pList(0)) + "} player2={" + playertoXml(
    game.pList(1)
  ) + "} currentstate={" + game.currentstate.toString + "} ERROR={" + game.ERROR + "} cardStack={" + game.cardStack.toString + "} midCard={" + playertoXml(
    game.midCard
  ) + "} winner={" + game.winner + "}"

def gametoXml2(game: gameInterface) =
  <game>
        player1={playertoXml(game.pList(0))}
        player2={playertoXml(game.pList(1))}
        currentstate={game.currentstate}
        ERROR={game.ERROR}
        cardStack={game.cardStack.toString}
        midCard={playertoXml(game.midCard)}
        winner={game.winner}
  </game>

var game = new Game("P1", "P2", between21State)
game = game.init()
val gamexml = gametoXml2(game)
print(gamexml)
gameString(game)

game.cardStack.toString

def playertoXml(player: Player) =
  <player> name={player.name} karten={
    player.karten.map(_.toString).map("" + _ + " ")
  } placed={player.placed}</player>

val p1 = new Player("Test", Vector[Card](R0, R1), false)

val xxml = playertoXml(p1)

print(playertoXml(p1))

1 + 2

def load: gameInterface = {
  var game: gameInterface = null
  val file = scala.xml.XML.loadFile("game.xml")

  val player1 = (file \\ "game" \ "player1")
  val p1N = (player1 \ "name").toString
  val p1V = (player1 \ "karten").toString
  val V1 = toCardVector(p1V)
  var p1P = false
  (player1 \ "placed").toString match
    case "true"  => p1P = true
    case "false" => p1P = false
  val player2 = (file \\ "game" \ "player2")
  val p2N = (player2 \ "name").toString
  val p2V = (player2 \ "karten").toString
  val V2 = toCardVector(p2V)
  var p2P = false
  (player2 \ "placed").toString match
    case "true"  => p2P = true
    case "false" => p2P = false
  var currentstate: State = between21State
  (file \\ "game" \ "currentstate").toString match
    case "between12State" => currentstate = between12State
    case "between21State" => currentstate = between21State
    case "player1State"   => currentstate = player1State
    case "player2State"   => currentstate = player2State
    case "winState"       => currentstate = winState
  val ERROR = (file \\ "game" \ "ERROR").toString.toInt
  val cardStack: CardStack = new CardStack(
    toStackMap(
      (file \\ "game" \ "cardStack").toString
    )
  )

  val midCard = (file \\ "game" \ "midCard")
  val pmN = (midCard \ "name").toString
  val pmV = (midCard \ "karten").toString
  val VM = toCardVector(pmV)
  val winner = (file \\ "game" \ "winner").toString.toInt

  val p1 = new Player(p1N, V1, p1P)
  val p2 = new Player(p2N, V2, p2P)
  val pm = new Player(pmN, VM, false)
  game = new Game(List(p1, p2), currentstate, ERROR, cardStack, pm, winner)
  game
}

//val gxml: gameInterface = load

val sdf = "false"

sdf.toBoolean

//val a = load

val xx = "B0 B1 B2 B3 B4 B5 B6 B7 B8 B9"

def toCardVector(text: String): Vector[Card] =
  val tmp = text.split(" ")
  var cardstmp = new ListBuffer[Card]()
  for (i <- tmp) {
    cardstmp += getCard(i)
  }
  cardstmp.toVector

toCardVector(xx)

val cc = game.cardStack.toString

val va = cc.split(" ")
va.foreach { println }

def toStackMap(text: String): Map[Card, Int] =
  val tmp = text.split(" ")
  var sMap: Map[Card, Int] = Map[Card, Int]()
  for (i <- tmp) {
    var temp = i.split(",")
    sMap = sMap.updated(getCard(temp(0)), temp(1).toInt)
  }
  sMap

//toStackMap(cc)
game.cardStack.toString.dropRight(1)

game.pList(0).karten.toSeq

// Game: pList(player1,player2), currentstate, ERROR, cardStack, midCard(playerMID), winner
// Player: name, karten(vector), placed

/*

def gametoXml(game: gameInterface): Unit =
  <game>player1={playertoXml(game.pList(0))}
        player2={playertoXml(game.pList(1))}
        currentstate={game.currentstate}
        ERROR={game.ERROR}
        cardStack={game.cardStack.toString}
        midCard={playertoXml(game.midCard)}
        winner={game.winner}
  </game>
 */
