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
    val p1k: Vector[Card] =
      (for (i <- 0 until p1ktmp) yield {
        getCard((p1 \\ "cardv") (i).as[String])
      }).toVector
    val p1p = (p1 \ "placed").get.as[Boolean]
    val player1 = new Player(p1n, p1k, p1p)

    val p2 = (json \ "game" \ "player2").get
    val p2n = (p2 \ "name").get.as[String]
    val p2ktmp =
      (p2 \ "kartenzahl").get.as[Int]
    val p2k: Vector[Card] =
      (for (i <- 0 until p2ktmp) yield {
        getCard((p2 \\ "cardv") (i).as[String])
      }).toVector
    val p2p = (p2 \ "placed").get.as[Boolean]
    val player2 = new Player(p2n, p2k, p2p)

    val cs = (json \ "game" \ "currentstate").get.toString
    val csf = cs.replaceAll("\"", "")
    val currentstate = csf match
      case "between12State" => between12State
      case "between21State" => between21State
      case "player1State" => player1State
      case "player2State" => player2State
      case "winState" => winState
      case _ => between21State

    val ERROR = (json \ "game" \ "ERROR").get.as[Int]

    val mc = (json \ "game" \ "midCard").get
    val mcn = (mc \ "name").get.as[String]

    val mck: Vector[Card] = Vector(getCard((mc \\ "cardv") (0).as[String]))
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

  def vectorToJson(vec: Vector[Card]): JsValue =
    vec.map { card =>
      Json.obj(
        "cardv" -> card.toString
      )
    }.foldLeft(JsArray.empty)(_ :+ _)

  def mapToJson(m: Map[Card, Int]): JsValue =
    m.map { case (card, value) =>
      Json.obj(
        "cardv" -> card.toString,
        "value" -> value
      )
    }.foldLeft(JsArray.empty)(_ :+ _)

  def smthngToJson(tuple_list: List[(String, Int)]): JsValue =
    tuple_list.map { case (card_png, index) =>
      Json.obj(
        "card_png" -> card_png,
        "index" -> index.toString
      )
    }.foldLeft(JsArray.empty)(_ :+ _)

  def create_per_player(player: Player): List[(String, Int)] =
    player.karten.zipWithIndex.map { (c, ind) =>
      val color = c.getColor match {
        case CardColor.Red => "red"
        case CardColor.Blue => "blue"
        case CardColor.Green => "green"
        case CardColor.Yellow => "yellow"
        case CardColor.Black => "black"
        case CardColor.ErrorC => ""
      }
      val value = c.getValue match {
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
}
