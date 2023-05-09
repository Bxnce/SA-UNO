package fileIOComponent.database
/*Uno-Dependecies*/
import model.gameComponent.gameInterface
import fileIOComponent.JSONImpl.fileIO
import fileIOComponent.database.sqlTables.{GameTable, PlayerTable}
import model.gameComponent.gameInterface
import model.gameComponent.gameBaseImpl.{Game, Player, UnoState}
/*Libraries*/
import concurrent.duration.DurationInt
import java.sql.SQLNonTransientException
import play.api.libs.json.{JsObject, Json}
import scala.util.{Failure, Success, Try}
import scala.concurrent.{Await, Future}
import slick.lifted.TableQuery
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api.*

val WAIT_TIME = 5.seconds
val WAIT_DB = 5000

val WAIT_TIME = 5.seconds
val WAIT_DB = 5000

class SlickDAO extends DAOInterface {
  val fileIO = new fileIO()
  val databaseDB: String = sys.env.getOrElse("MYSQL_DATABASE", "uno")
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "nue")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "root")
  val databasePort: String = sys.env.getOrElse("MYSQL_PORT", "3306")
  val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "localhost")
  val databaseUrl = s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"
  println(databaseUrl)
  val database = Database.forURL(
    url = databaseUrl,
    driver = "org.postgresql.Driver",
    user = databaseUser,
    password = databasePassword
  )

  val player = new TableQuery(new PlayerTable(_))
  val gameTable = new TableQuery(new GameTable(_))

  val setup: DBIOAction[Unit, NoStream, Effect.Schema] = DBIO.seq(player.schema.createIfNotExists, gameTable.schema.createIfNotExists)
  println("create tables")
  try {
    Await.result(database.run(setup), WAIT_TIME)
  } catch  {
    case e: SQLNonTransientException =>
      println("Waiting for DB connection")
      Thread.sleep(WAIT_DB)
      Await.result(database.run(setup), WAIT_TIME)
  }
  println("tables created")

  override def save(saveGame: gameInterface): Unit =
    Try {
      println("saving game in postgres DB")
      val jsonGame = fileIO.gameToJson(saveGame)
      val player1Id =
        storePlayer(
          (jsonGame \ "game" \ "player1" \ "name").get.toString(),
          (jsonGame \ "game" \ "player1" \ "karten").get.toString(),
          (jsonGame \ "game" \ "player1" \ "kartenzahl").get.toString().toInt,
          (jsonGame \ "game" \ "player1" \ "placed").get.toString().toBoolean
        )

      val player2Id =
        storePlayer(
          (jsonGame \ "game" \ "player2" \ "name").get.toString(),
          (jsonGame \ "game" \ "player2" \ "karten").get.toString(),
          (jsonGame \ "game" \ "player2" \ "kartenzahl").get.toString().toInt,
          (jsonGame \ "game" \ "player2" \ "placed").get.toString().toBoolean,
        )

      val midcardId =
        storePlayer(
          (jsonGame \ "game" \ "midCard" \ "name").get.toString(),
          (jsonGame \ "game" \ "midCard" \ "karten").get.toString(),
          0,
          (jsonGame \ "game" \ "midCard" \ "placed").get.toString().toBoolean,
        )

      val gameId =
        storeGame(
          player1Id,
          player2Id,
          midcardId,
          (jsonGame \ "game" \ "currentstate").get.toString(),
          (jsonGame \ "game" \ "ERROR").get.toString().toInt,
          (jsonGame \ "game" \ "cardstack").get.toString(),
          (jsonGame \ "game" \ "winner").get.toString().toInt,
        )
      println(s"Game saved in postgres DB with ID $gameId")
    }

  override def load(id: Option[Int] = None): Try[gameInterface] =
    Try {
      val query = id.map(id => gameTable.filter(_.id === id))
        .getOrElse(gameTable.filter(_.id === gameTable.map(_.id).max))

      val game = Await.result(database.run(query.result), WAIT_TIME)
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
    }


  override def storePlayer(name: String, cards: String, card_count: Int, placed: Boolean): Int =
    val playerS = (0, name, cards, card_count, placed)
    Await.result(database.run(player returning player.map(_.id) += playerS), WAIT_TIME)

  override def storeGame(player1: Int, player2: Int, midCard: Int, currentstate: String, error: Int, cardstack: String, winner: Int): Int =
    val game = (0, player1, player2, midCard, currentstate, error, cardstack, winner)
    Await.result(database.run(gameTable returning gameTable.map(_.id) += game), WAIT_TIME)

  override def updateGame(id: Int, player1: Option[Int], player2: Option[Int], midCard: Option[Int], currentstate: Option[String], error: Option[Int], cardstack: Option[String], winner: Option[Int]): Try[Boolean] =
    Try {
      val p1Query =
        player1 match {
          case Some(player1) => gameTable.filter(_.id === id).map(_.player1Id).update(player1)
          case None => DBIO.successful(0)
        }
      val p2Query =
        player2 match {
          case Some(player2) => gameTable.filter(_.id === id).map(_.player2Id).update(player2)
          case None => DBIO.successful(0)
        }
      val midQuery =
        midCard match {
          case Some(midCard) => gameTable.filter(_.id === id).map(_.midcardId).update(midCard)
          case None => DBIO.successful(0)
        }
      val currentStateQuery =
        currentstate match {
          case Some(currentstate) => gameTable.filter(_.id === id).map(_.currentstate).update(currentstate)
          case None => DBIO.successful(0)
        }
      val errorQuery =
        error match {
          case Some(error) => gameTable.filter(_.id === id).map(_.error).update(error)
          case None => DBIO.successful(0)
        }
      val cardStackQuery =
        cardstack match {
          case Some(cardstack) => gameTable.filter(_.id === id).map(_.cardstack).update(cardstack)
          case None => DBIO.successful(0)
        }
      val winnerQuery =
        winner match {
          case Some(winner) => gameTable.filter(_.id === id).map(_.winner).update(winner)
          case None => DBIO.successful(0)
        }
      val query = p1Query andThen p2Query andThen midQuery andThen currentStateQuery andThen errorQuery andThen cardStackQuery andThen winnerQuery
      Await.result(database.run(query), WAIT_TIME)
      true
    }


  override def updatePlayer(id: Int, name: Option[String], cards: Option[String], card_count: Option[Int], placed: Option[Boolean]): Try[Boolean] =
    Try {
      val nameQuery =
        name match {
          case Some(name) => player.filter(_.id === id).map(_.name).update(name)
          case None => DBIO.successful(0)
        }
      val cardsQuery =
        cards match {
          case Some(cards) => player.filter(_.id === id).map(_.cards).update(cards)
          case None => DBIO.successful(0)
        }
      val cardCountQuery =
        card_count match {
          case Some(card_count) => player.filter(_.id === id).map(_.card_count).update(card_count)
          case None => DBIO.successful(0)
        }
      val placedQuery =
        placed match {
          case Some(placed) => player.filter(_.id === id).map(_.placed).update(placed)
          case None => DBIO.successful(0)
        }
      val query = nameQuery andThen cardsQuery andThen cardCountQuery andThen placedQuery
      Await.result(database.run(query), WAIT_TIME)
      true
    }

  override def deleteGame(id: Int): Try[Boolean] =
    Try{
      Await.result(database.run(gameTable.filter(_.id === id).delete), WAIT_TIME)
      true
    }

  override def deletePlayer(id: Int): Try[Boolean] =
    Try{
      Await.result(database.run(player.filter(_.id === id).delete), WAIT_TIME)
      true
    }

  def queryPlayer(id: Int): String =
    val playerQuery = player.filter(_.id === id).result
    val playerRes = Await.result(database.run(playerQuery), WAIT_TIME)
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
      .replace("\"\"", "\"")
}
// <3