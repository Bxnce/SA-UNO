package de.htwg.se.uno
package model.fileIOComponent.JSONImpl

import model.gameComponent.gameInterface
import model.fileIOComponent.FileIOInterface
import model.gameComponent.gameBaseImpl.{CardStack, Card, Game, Player}
import model.gameComponent.gameBaseImpl.toCard._
import controller.controllerComponent.controllerBaseImpl.{
  player1State,
  player2State,
  between12State,
  between21State,
  winState
}
import model.gameComponent.gameBaseImpl.CardValue
import model.gameComponent.gameBaseImpl.CardColor
import util.State
import java.io._
import play.api.libs.json._
import scala.io.Source
import scala.collection.mutable.ListBuffer


class fileIO extends FileIOInterface {

  override def load: gameInterface =
    val source: String = Source.fromFile("game.json").getLines.mkString
    val json: JsValue = Json.parse(source)

    val p1 = (json \ "game" \ "player1").get
    val p1n = (p1 \ "name").get.as[String]
    val p1ktmp = (p1 \ "kartenzahl").get.as[Int]
    var p1k: Vector[Card] =
      (for (i <- 0 until p1ktmp) yield {
        getCard((p1 \\ "cardv")(i).as[String])
      }).toVector
    val p1p = (p1 \ "placed").get.as[Boolean]
    val player1 = new Player(p1n, p1k, p1p)

    val p2 = (json \ "game" \ "player2").get
    val p2n = (p2 \ "name").get.as[String]
    val p2ktmp =
      (p2 \ "kartenzahl").get.as[Int]
    var p2k: Vector[Card] =
      (for (i <- 0 until p2ktmp) yield {
        getCard((p2 \\ "cardv")(i).as[String])
      }).toVector
    val p2p = (p2 \ "placed").get.as[Boolean]
    val player2 = new Player(p2n, p2k, p2p)

    val cs = (json \ "game" \ "currentstate").get.toString
    val csf = cs.replaceAll("\"", "")
    var currentstate: State = between21State
    csf match
      case "between12State" => currentstate = between12State
      case "between21State" => currentstate = between21State
      case "player1State"   => currentstate = player1State
      case "player2State"   => currentstate = player2State
      case "winState"       => currentstate = winState

    val ERROR = (json \ "game" \ "ERROR").get.as[Int]

    val mc = (json \ "game" \ "midCard").get
    val mcn = (mc \ "name").get.as[String]

    var mck: Vector[Card] = Vector(getCard((mc \\ "cardv")(0).as[String]))
    val mcp = (mc \ "placed").get.as[Boolean]
    val midcard = new Player(mcn, mck, mcp)

    val winner = (json \ "game" \ "winner").get.as[Int]

    val game = new Game(
      List(player1, player2),
      currentstate,
      ERROR,
      new CardStack(
        Card.values.map(x => (x, 2)).toMap
      ),
      midcard,
      winner
    )
    game

  override def save(game: gameInterface): Unit =
    val pw = new PrintWriter(new File("game.json"))
    pw.write(Json.prettyPrint(gameToJson(game)))
    pw.close

  def return_json(game: gameInterface): String =
    val game_j = Json.prettyPrint(gameToJson(game))
    game_j

  def gameToJson(game: gameInterface) = {
    Json.obj(
      "game" -> Json.obj(
        "player1" -> Json.obj(
          "name" -> game.pList(0).name,
          "karten" -> vectorToJson(game.pList(0).karten),
          "kartenzahl" -> game.pList(0).karten.size,
          "placed" -> game.pList(0).placed,
          "png_ind" -> smthngToJson(create_per_player(game.pList(0)))
        ),
        "player2" -> Json.obj(
          "name" -> game.pList(1).name,
          "karten" -> vectorToJson(game.pList(1).karten),
          "kartenzahl" -> game.pList(1).karten.size,
          "placed" -> game.pList(1).placed,
          "png_ind" -> smthngToJson(create_per_player(game.pList(1)))
        ),
        "currentstate" -> game.currentstate.toString,
        "ERROR" -> game.ERROR,
        "cardstack" -> mapToJson(game.cardStack.cards),
        "midCard" -> Json.obj(
          "name" -> game.midCard.name,
          "karten" -> vectorToJson(game.midCard.karten),
          "placed" -> game.midCard.placed,
          "png_ind" -> smthngToJson(create_per_player(game.midCard))
        ),
        "winner" -> game.winner
      )
    )
  }

  def vectorToJson(vec: Vector[Card]) =
    Json.toJson(
      for {
        i <- vec
      } yield {
        Json.obj(
          "cardv" -> i.toString
        )
      }
    )

  def mapToJson(m: Map[Card, Int]) =
    Json.toJson(
      for { i <- m } yield {
        Json.obj(
          "cardv" -> i(0).toString,
          "value" -> i(1)
        )
      }
    )

  def smthngToJson(tuple_list: List[(String, Int)]) =
    Json.toJson(
        for { i <- tuple_list } yield {
          Json.obj(
            "card_png" -> i(0),
            "index" -> i(1).toString
          )
        }
      )

  def create_per_player(player: Player): List[(String, Int)] =
    var cards = new ListBuffer[(String, Int)]()
    var ind = 0
    for (c <- player.karten) {
      var color = ""
      var value = ""
      c.getColor match {
        case CardColor.Red => color = "red"
        case CardColor.Blue => color = "blue"
        case CardColor.Green => color = "green"
        case CardColor.Yellow => color = "yellow"
        case CardColor.Black => color = "black"
        case CardColor.ErrorC => color = ""
      }
      c.getValue match {
        case CardValue.Zero => value = "_0"
        case CardValue.One => value = "_1"
        case CardValue.Two => value = "_2"
        case CardValue.Three => value = "_3"
        case CardValue.Four => value = "_4"
        case CardValue.Five => value = "_5"
        case CardValue.Six => value = "_6"
        case CardValue.Seven => value = "_7"
        case CardValue.Eight => value = "_8"
        case CardValue.Nine => value = "_9"
        case CardValue.Take2 => value = "_+2"
        case CardValue.Skip => value = "_skip"
        case CardValue.Wildcard => value = "_wildcard"
        case CardValue.Take4 => value = "_+4"
        case CardValue.Special => value = ""
        case CardValue.Error => value = ""
      }
      var card_s = "cards/" + color + value + ".png"
      var tup = (card_s, ind)
      cards += tup
      ind += 1
    }
    cards.toList
}
