package fileIOComponent.JSONImpl

import fileIOComponent.FileIOInterface
import java.io._
import play.api.libs.json._
import scala.io.Source
import scala.collection.mutable.ListBuffer
import model.gameComponent.gameInterface
import model.gameComponent.gameBaseImpl._
import model.gameComponent.gameBaseImpl.toCard.getCard



class fileIO extends FileIOInterface {

  override def load: gameInterface =
    val source: String = Source.fromFile("game.json").getLines.mkString
    val json: JsValue = Json.parse(source)

    val p1 = (json \ "game" \ "player1").get
    val p1n = (p1 \ "name").get.as[String]
    val p1ktmp = (p1 \ "kartenzahl").get.as[Int]
    val p1k: Vector[Card] =
      (0 until p1ktmp).map { i =>
        getCard((p1 \ "cardv") (i).as[String])
      }.toVector
    val p1p = (p1 \ "placed").get.as[Boolean]
    val player1 = Player(p1n, p1k, p1p)

    val p2 = (json \ "game" \ "player2").get
    val p2n = (p2 \ "name").get.as[String]
    val p2ktmp =
      (p2 \ "kartenzahl").get.as[Int]
    val p2k: Vector[Card] =
      (0 until p2ktmp).map { i =>
        getCard((p2 \ "cardv") (i).as[String])
      }.toVector
    val p2p = (p2 \ "placed").get.as[Boolean]
    val player2 = Player(p2n, p2k, p2p)

    val cs = (json \ "game" \ "currentstate").get.toString
    val csf = cs.replaceAll("\"", "")
    val currentstate = csf match
      case "between12State" => UnoState.between12State
      case "between21State" => UnoState.between21State
      case "player1State" => UnoState.player1State
      case "player2State" => UnoState.player2State
      case "winState" => UnoState.winState
      case _ => UnoState.between21State

    val ERROR = (json \ "game" \ "ERROR").get.as[Int]

    val mc = (json \ "game" \ "midCard").get
    val mcn = (mc \ "name").get.as[String]

    val mck: Vector[Card] = Vector(getCard((mc \\ "cardv").head.as[String]))
    val mcp = (mc \ "placed").get.as[Boolean]
    val midcard = Player(mcn, mck, mcp)

    val winner = (json \ "game" \ "winner").get.as[Int]

    val game = Game(
      List(player1, player2),
      currentstate,
      ERROR,
      CardStack(
        Card.values.map(x => (x, 2)).toMap
      ),
      midcard,
      winner
    )
    game

  override def save(game: gameInterface): Unit =
    val pw = new PrintWriter(new File("game.json"))
    pw.write(Json.prettyPrint(gameToJson(game)))
    pw.close()

  def return_json(game: gameInterface): String =
    val game_j = Json.prettyPrint(gameToJson(game))
    game_j

  def gameToJson(game: gameInterface): JsObject = {
    Json.obj(
      "game" -> Json.obj(
        "player1" -> Json.obj(
          "name" -> game.pList.head.name,
          "karten" -> vectorToJson(game.pList.head.karten),
          "kartenzahl" -> game.pList.head.karten.size,
          "placed" -> game.pList.head.placed,
          "png_ind" -> smthngToJson(create_per_player(game.pList.head))
        ),
        "player2" -> Json.obj(
          "name" -> game.pList(1).name,
          "karten" -> vectorToJson(game.pList(1).karten),
          "kartenzahl" -> game.pList(1).karten.size,
          "placed" -> game.pList(1).placed,
          "png_ind" -> smthngToJson(create_per_player(game.pList(1)))
        ),
        "currentstate" -> game.currentstate.string_repr,
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
      val card_s = s"cards/$color$value.png"
      (card_s, ind)
    }.toList
}