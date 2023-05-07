package fileIOComponent.database

import slick.lifted.TableQuery
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*
import fileIOComponent.database.sqlTables.{GameTable, PlayerTable}
import fileIOComponent.JSONImpl.fileIO
import model.gameComponent.gameInterface

import concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import model.gameComponent.gameBaseImpl.{Game, Player, UnoState}
import play.api.libs.json.{JsObject, Json}
// TODO implement load and cleanup this whole fucking dumb shit
class Slick extends DAOInterface {
  val fileIO = new fileIO()
  val databaseDB: String = sys.env.getOrElse("MYSQL_DATABASE", "uno")
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "nue")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "root")
  val databasePort: String = sys.env.getOrElse("MYSQL_PORT", "3306")
  val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "localhost")
  val databaseUrl = s"jdbc:mysql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
  println(databaseUrl)
  val database = Database.forURL(
    url = databaseUrl,
    driver = "com.mysql.cj.jdbc.Driver",
    user = databaseUser,
    password = databasePassword
  )

  val player = new TableQuery(new PlayerTable(_))
  val gameTable = new TableQuery(new GameTable(_))

  val setup: DBIOAction[Unit, NoStream, Effect.Schema] = DBIO.seq(player.schema.createIfNotExists, gameTable.schema.createIfNotExists)
  println("create tables")
  Await.result(database.run(setup), 5.seconds)
  println("tables created")

  override def save(saveGame: gameInterface): Unit =
    println("saving game in MySQL")
    val jsonGame = fileIO.gameToJson(saveGame)
    val player1 = (
      0,
      (jsonGame \ "game" \ "player1" \ "name").get.toString(),
      (jsonGame \ "game" \ "player1" \ "karten").get.toString(),
      (jsonGame \ "game" \ "player1" \ "kartenzahl").get.toString().toInt,
      (jsonGame \ "game" \ "player1" \ "placed").get.toString().toBoolean
    )
    val player1IdQuery = (player returning player.map(_.id)) += player1
    val player1Id = Await.result(database.run(player1IdQuery), 2.seconds)


    val player2 = (
      0,
      (jsonGame \ "game" \ "player2" \ "name").get.toString(),
      (jsonGame \ "game" \ "player2" \ "karten").get.toString(),
      (jsonGame \ "game" \ "player2" \ "kartenzahl").get.toString().toInt,
      (jsonGame \ "game" \ "player2" \ "placed").get.toString().toBoolean,
    )
    val player2IdQuery = (player returning player.map(_.id)) += player2
    val player2Id = Await.result(database.run(player2IdQuery), 2.seconds)


    val midcard = (
      0,
      (jsonGame \ "game" \ "midCard" \ "name").get.toString(),
      (jsonGame \ "game" \ "midCard" \ "karten").get.toString(),
      0,
      (jsonGame \ "game" \ "midCard" \ "placed").get.toString().toBoolean,
    )
    val midcardIdQuery = (player returning player.map(_.id)) += midcard
    val midcardId = Await.result(database.run(midcardIdQuery), 2.seconds)


    val game = (
      0,
      player1Id,
      player2Id,
      midcardId,
      (jsonGame \ "game" \ "currentstate").get.toString(),
      (jsonGame \ "game" \ "ERROR").get.toString().toInt,
      (jsonGame \ "game" \ "cardstack").get.toString(),
      (jsonGame \ "game" \ "winner").get.toString().toInt,
    )
    val gameIdQuery = (gameTable returning gameTable.map(_.id)) += game
    val gameId = Await.result(database.run(gameIdQuery), 2.seconds)
    println(s"Game saved in MySQL with ID $gameId")


  override def load(id: Option[Int] = None): gameInterface =

    val query = id match
      case Some(id) =>
        gameTable.filter(_.id === id).result
      case None =>
        gameTable.sortBy(_.id.desc).take(1).result

    val game = Await.result(database.run(query), 5.seconds)
    val player1 = queryPlayer(game.head._2)
    val player2 = queryPlayer(game.head._3)
    val midcard = queryPlayer(game.head._4)
    val currentstate = sanitize(game.head._5)
    val ERROR = sanitize(game.head._6.toString)
    val cardstack = sanitize(game.head._7)
    val winner = sanitize(game.head._8.toString)

    val resString =
      s"""{"game" : {"player1" : $player1,
                   "player2" : $player2,
                   "currentstate" : $currentstate,
                   "ERROR" : $ERROR,
                   "cardstack" : $cardstack,
                   "midCard" : $midcard,
                   "winner" : $winner}}"""
    fileIO.jsonToGame(resString)


  def queryPlayer(id: Int): String =
    val playerQuery = player.filter(_.id === id).result
    val playerRes = Await.result(database.run(playerQuery), 5.seconds)
    val name = playerRes.head._2
    val karten = playerRes.head._3
    val kartenzahl = playerRes.head._4
    val placed = playerRes.head._5
    sanitize(s"""{"name" : "$name", "karten" : $karten, "kartenzahl" : $kartenzahl, "placed" : $placed}""")


  def sanitize(str: String): String =
    str.replace("\\n", "\n")
       .replace("\\r", "\r")
       .replace("\\t", "\t")
       .replace("\\b", "\b")
       .replace("\\f", "\f")
       .replace("\\\\", "\\")
       .replace("\\\"", "\"")
       .replace("\\'", "'")
       .replace("\"\"","\"")
}
// <3