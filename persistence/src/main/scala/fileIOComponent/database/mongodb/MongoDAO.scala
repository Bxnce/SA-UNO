package fileIOComponent.database.mongodb

import com.google.inject.Inject
import fileIOComponent.JSONImpl.fileIO
import fileIOComponent.database.mongodb.{getHighestId, handleResult}
import fileIOComponent.database.{DAOInterface, WAIT_TIME}
import model.gameComponent.gameInterface
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.*
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable, result}
import org.mongodb.scala.documentToUntypedDocument

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

class MongoDAO @Inject() extends DAOInterface {
  private val fio = new fileIO
  /* Init */
  private val database_pw = sys.env.getOrElse("MONGO_ROOT_PASSWORD", "mongo")
  private val database_username = sys.env.getOrElse("MONGO_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_HOST", "localhost")
  private val port = sys.env.getOrElse("MONGO_PORT", "27017")

  val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"
  private val client: MongoClient = MongoClient(uri)
  println(uri)

  val db: MongoDatabase = client.getDatabase("uno")
  var gameCollection: MongoCollection[Document] = db.getCollection("game")
  var playerCollection: MongoCollection[Document] = db.getCollection("player")

  println("Connected to MongoDB")

  override def save(game: gameInterface): Unit = {
    println("Saving game to MongoDB")
    val gameJson = fio.gameToJson(game)
    val highestCurrentPlayerId = getHighestId(playerCollection)
    val highestCurrentGameId = getHighestId(gameCollection)

    val player1Id = highestCurrentPlayerId + 1
    val player1Document = Document(
      "_id" -> player1Id,
      "name" -> (gameJson \ "game" \ "player1" \ "name").get.toString.replaceAll("\"", ""),
      "cards" -> (gameJson \ "game" \ "player1" \ "karten").get.toString(),
      "card_count" -> (gameJson \ "game" \ "player1" \ "kartenzahl").get.toString().toInt,
      "placed" -> (gameJson \ "game" \ "player1" \ "placed").get.toString().toBoolean
    )

    val player2Id = highestCurrentPlayerId + 2
    val player2Document = Document(
      "_id" -> player2Id,
      "name" -> (gameJson \ "game" \ "player2" \ "name").get.toString.replaceAll("\"", ""),
      "cards" -> (gameJson \ "game" \ "player2" \ "karten").get.toString(),
      "card_count" -> (gameJson \ "game" \ "player2" \ "kartenzahl").get.toString().toInt,
      "placed" -> (gameJson \ "game" \ "player2" \ "placed").get.toString().toBoolean
    )

    val midCardId = highestCurrentPlayerId + 3
    val midCardDocument = Document(
      "_id" -> midCardId,
      "name" -> (gameJson \ "game" \ "midCard" \ "name").get.toString.replaceAll("\"", ""),
      "cards" -> (gameJson \ "game" \ "midCard" \ "karten").get.toString(),
      "card_count" -> 0,
      "placed" -> (gameJson \ "game" \ "midCard" \ "placed").get.toString().toBoolean
    )

    val gameId = highestCurrentGameId + 1
    val gameDocument: Document = Document(
      "_id" -> gameId,
      "player1" -> player1Document,
      "player2" -> player2Document,
      "midCard" -> midCardDocument,
      "currentstate" -> (gameJson \ "game" \ "currentstate").get.toString().replaceAll("\"", ""),
      "error" -> (gameJson \ "game" \ "ERROR").get.toString().toInt,
      "cardstack" -> (gameJson \ "game" \ "cardstack").get.toString(),
      "winner" -> (gameJson \ "game" \ "winner").get.toString().toInt
    )

    handleResult(playerCollection.insertOne(player1Document))
    handleResult(playerCollection.insertOne(player2Document))
    handleResult(playerCollection.insertOne(midCardDocument))
    handleResult(gameCollection.insertOne(gameDocument))
    println(s"Inserted game in MongoDB with id $gameId")
  }

  override def load(id: Option[Int]): Try[gameInterface] =
    Try {
      val updateId = id match {
        case Some(id) => id
        case None => getHighestId(gameCollection)
      }
      val gameDocument = Await.result(gameCollection.find(equal("_id", updateId)).first().head(), WAIT_TIME)

      val player1 = queryPlayer(gameDocument.get("player1") match {
        case Some(player) => player.asDocument()
        case None => throw new Exception("Player1 not found")
      }
      )

      val player2 = queryPlayer(gameDocument.get("player2") match {
        case Some(player) => player.asDocument()
        case None => throw new Exception("Player2 not found")
      }
      )

      val midcard = queryPlayer(gameDocument.get("midCard") match {
        case Some(player) => player.asDocument()
        case None => throw new Exception("midCard not found")
      }
      )
      val currentstate = gameDocument.getString("currentstate")
      val ERROR = gameDocument.getInteger("ERROR")
      val cardstack = gameDocument.getString("cardstack").toString
      val winner = gameDocument.getInteger("winner")

      val resString =
        s"""{"game" : {"player1" : $player1,
                   "player2" : $player2,
                   "currentstate" : "$currentstate",
                   "ERROR" : ${ERROR.toInt},
                   "cardstack" : $cardstack,
                   "midCard" : $midcard,
                   "winner" : $winner}}"""
      fio.jsonToGame(resString)
    }

  override def updateGame(id: Int, player1: Option[Int] = None, player2: Option[Int] = None, midCard: Option[Int] = None, currentstate: Option[String] = None, error: Option[Int] = None, cardstack: Option[String] = None, winner: Option[Int] = None): Try[Boolean] =
    Try {
      player1 match {
        case Some(player1) => handleResult(gameCollection.updateOne(equal("_id", id), Updates.set("player1",
          Await.result(playerCollection.find(equal("_id", player1)).first().head(), WAIT_TIME).get("player1") match {
            case Some(player) =>
              player.asDocument()
            case None => throw new Exception("Player1 not updated")
          })))
        case None =>
      }
      player2 match {
        case Some(player2) => handleResult(gameCollection.updateOne(equal("_id", id), Updates.set("player2",
          Await.result(playerCollection.find(equal("_id", player2)).first().head(), WAIT_TIME).get("player2") match {
            case Some(player) => player.asDocument()
            case None => throw new Exception("Player2 not updated")
          })))
        case None =>
      }
      midCard match {
        case Some(midCard) => handleResult(gameCollection.updateOne(equal("_id", id), Updates.set("midCard",
          Await.result(playerCollection.find(equal("_id", midCard)).first().head(), WAIT_TIME).get("midCard") match {
            case Some(player) => player.asDocument()
            case None => throw new Exception("midCard not updated")
          })))
        case None =>
      }
      currentstate match {
        case Some(currentstate) => handleResult(gameCollection.updateOne(equal("_id", id), Updates.set("currentstate", currentstate)))
        case None =>
      }
      error match {
        case Some(error) => handleResult(gameCollection.updateOne(equal("_id", id), Updates.set("error", error)))
        case None =>
      }
      cardstack match {
        case Some(cardstack) => handleResult(gameCollection.updateOne(equal("_id", id), Updates.set("cardstack", cardstack)))
        case None =>
      }
      winner match {
        case Some(winner) => handleResult(gameCollection.updateOne(equal("_id", id), Updates.set("winner", winner)))
        case None =>
      }
      true
    }

  override def updatePlayer(id: Int, name: Option[String] = None, cards: Option[String] = None, card_count: Option[Int] = None, placed: Option[Boolean] = None): Try[Boolean] =
    Try {
      name match {
        case Some(name) => handleResult(playerCollection.updateOne(equal("_id", id), Updates.set("name", name)))
        case None =>
      }
      cards match {
        case Some(cards) => handleResult(playerCollection.updateOne(equal("_id", id), Updates.set("cards", cards)))
        case None =>
      }
      card_count match {
        case Some(card_count) => handleResult(playerCollection.updateOne(equal("_id", id), Updates.set("card_count", card_count)))
        case None =>
      }
      placed match {
        case Some(placed) => handleResult(playerCollection.updateOne(equal("_id", id), Updates.set("placed", placed)))
        case None =>
      }
      true
    }

  override def deleteGame(id: Int): Try[Boolean] =
    Try {
      handleResult(gameCollection.deleteOne(equal("_id", id)))
      true
    }

  override def deletePlayer(id: Int): Try[Boolean] =
    Try {
      handleResult(playerCollection.deleteOne(equal("_id", id)))
      true
    }

  def queryPlayer(playerDoc: BsonDocument): String =
    val name = playerDoc.get("name").asString().getValue
    val karten = playerDoc.get("cards").asString().getValue
    val kartenzahl = playerDoc.get("card_count").asInt32().getValue
    val placed = playerDoc.get("placed").asBoolean().getValue
    s"""{"name" : "$name", "karten" : $karten, "kartenzahl" : $kartenzahl, "placed" : $placed}"""

}

