package fileIOComponent.database

import com.google.inject.Inject
import fileIOComponent.JSONImpl.fileIO
import fileIOComponent.database.{DAOInterface, WAIT_TIME}
import model.gameComponent.gameInterface
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.*
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable, result}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try


class MongoDAO @Inject() extends DAOInterface {
  private val fio = new fileIO
  /* Init */
  private val database_pw = sys.env.getOrElse("MONGO_INITDB_ROOT_PASSWORD", "mongo")
  private val database_username = sys.env.getOrElse("MONGO_INITDB_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_HOST", "localhost")
  private val port = sys.env.getOrElse("MONGO_PORT", "27017")

  val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"
  private val client: MongoClient = MongoClient(uri)
  println(uri)
  val db: MongoDatabase = client.getDatabase("uno")

  private val gameCollection: MongoCollection[Document] = db.getCollection("game")
  private val playerCollection: MongoCollection[Document] = db.getCollection("player")
  println("Connected to MongoDB")

  override def save(game: gameInterface): Unit =
    println("Saving game to MongoDB")
    val gameJson = fio.gameToJson(game)
    val highestCurrentPlayerId = getHighestId(playerCollection)
    val highestCurrentGameId = getHighestId(gameCollection)

    val player1Id = highestCurrentPlayerId + 1
    val player1Document = Document(
      "_id" -> player1Id,
      "name" -> (gameJson \ "game" \ "player1" \ "name").get.toString(),
      "cards" -> (gameJson \ "game" \ "player1" \ "karten").get.toString(),
      "card_count" -> (gameJson \ "game" \ "player1" \ "kartenzahl").get.toString().toInt,
      "placed" -> (gameJson \ "game" \ "player1" \ "placed").get.toString().toBoolean
    )

    val player2Id = highestCurrentPlayerId + 2
    val player2Document = Document(
      "_id" -> player2Id,
      "name" -> (gameJson \ "game" \ "player2" \ "name").get.toString(),
      "cards" -> (gameJson \ "game" \ "player2" \ "karten").get.toString(),
      "card_count" -> (gameJson \ "game" \ "player2" \ "kartenzahl").get.toString().toInt,
      "placed" -> (gameJson \ "game" \ "player2" \ "placed").get.toString().toBoolean
    )

    val midCardId = highestCurrentPlayerId + 3
    val midCardDocument = Document(
      "_id" -> midCardId,
      "name" -> (gameJson \ "game" \ "midCard" \ "name").get.toString(),
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
      "currentstate" -> (gameJson \ "game" \ "currentstate").get.toString(),
      "error" -> (gameJson \ "game" \ "ERROR").get.toString().toInt,
      "cardstack" -> (gameJson \ "game" \ "cardstack").get.toString(),
      "winner" -> (gameJson \ "game" \ "winner").get.toString().toInt
    )
    insertOne(playerCollection.insertOne(player1Document))
    insertOne(playerCollection.insertOne(player2Document))
    insertOne(playerCollection.insertOne(midCardDocument))
    insertOne(gameCollection.insertOne(gameDocument))
    println(s"Inserted game in MongoDB with id $gameId")

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
      val currentstate = gameDocument.get("currentstate").toString
      val ERROR = gameDocument.get("ERROR").toString
      val cardstack = gameDocument.get("cardstack").toString
      val winner = gameDocument.get("winner").toString

      val resString =
        s"""{"game" : {"player1" : $player1,
                   "player2" : $player2,
                   "currentstate" : $currentstate,
                   "ERROR" : $ERROR,
                   "cardstack" : $cardstack,
                   "midCard" : $midcard,
                   "winner" : $winner}}"""
      fio.jsonToGame(resString)
    }

  override def updateGame(id: Int, player1: Option[Int], player2: Option[Int], midCard: Option[Int], currentstate: Option[String], error: Option[Int], cardstack: Option[String], winner: Option[Int]): Try[Boolean] =
    Try {
      player1 match {
        case Some(player1) => updateOne(gameCollection.updateOne(equal("_id", id), Updates.set("player1",
          Await.result(gameCollection.find(equal("_id", player1)).first().head(), WAIT_TIME).get("player1") match {
            case Some(player) =>
              player.asDocument()
            case None => throw new Exception("Player1 not updated")
          })))
        case None =>
      }
      player2 match {
        case Some(player2) => updateOne(gameCollection.updateOne(equal("_id", id), Updates.set("player2",
          Await.result(gameCollection.find(equal("_id", player2)).first().head(), WAIT_TIME).get("player2") match {
            case Some(player) => player.asDocument()
            case None => throw new Exception("Player2 not updated")
          })))
        case None =>
      }
      midCard match {
        case Some(midCard) => updateOne(gameCollection.updateOne(equal("_id", id), Updates.set("midCard",
          Await.result(gameCollection.find(equal("_id", midCard)).first().head(), WAIT_TIME).get("midCard") match {
            case Some(player) => player.asDocument()
            case None => throw new Exception("midCard not updated")
          })))
        case None =>
      }
      currentstate match {
        case Some(currentstate) => updateOne(gameCollection.updateOne(equal("_id", id), Updates.set("currentstate", currentstate)))
        case None =>
      }
      error match {
        case Some(error) => updateOne(gameCollection.updateOne(equal("_id", id), Updates.set("error", error)))
        case None =>
      }
      cardstack match {
        case Some(cardstack) => updateOne(gameCollection.updateOne(equal("_id", id), Updates.set("cardstack", cardstack)))
        case None =>
      }
      winner match {
        case Some(winner) => updateOne(gameCollection.updateOne(equal("_id", id), Updates.set("winner", winner)))
        case None =>
      }
      true
    }

  override def updatePlayer(id: Int, name: Option[String], cards: Option[String], card_count: Option[Int], placed: Option[Boolean]): Try[Boolean] =
    Try {
      name match {
        case Some(name) => updateOne(playerCollection.updateOne(equal("_id", id), Updates.set("name", name)))
        case None =>
      }
      cards match {
        case Some(cards) => updateOne(playerCollection.updateOne(equal("_id", id), Updates.set("cards", cards)))
        case None =>
      }
      card_count match {
        case Some(card_count) => updateOne(playerCollection.updateOne(equal("_id", id), Updates.set("card_count", card_count)))
        case None =>
      }
      placed match {
        case Some(placed) => updateOne(playerCollection.updateOne(equal("_id", id), Updates.set("placed", placed)))
        case None =>
      }
      true
    }

  override def deleteGame(id: Int): Try[Boolean] =
    Try {
      deleteOne(gameCollection.deleteOne(equal("_id", id)))
      true
    }

  override def deletePlayer(id: Int): Try[Boolean] =
    Try {
      deleteOne(playerCollection.deleteOne(equal("_id", id)))
      true
    }

  private def getHighestId(coll: MongoCollection[Document]): Int =
    // Aggregations-Pipeline erstellen, um das höchste _id-Feld zurückzugeben
    val pipeline = Seq(
      Aggregates.sort(Sorts.descending("_id")),
      Aggregates.limit(1),
      Aggregates.project(Document("_id" -> 1))
    )
    // Aggregations-Pipeline ausführen
    val observable: Observable[Document] = coll.aggregate(pipeline)
    val futureResult = observable.headOption()
    // Höchste ID extrahieren und zurückgeben
    val result = Await.result(futureResult, WAIT_TIME)
    result.flatMap(_.get("_id").map(_.asInt32().getValue.toHexString)).getOrElse("0").toInt

  private def insertOne(insertObs: SingleObservable[InsertOneResult]): Unit =
    insertObs.subscribe(new Observer[InsertOneResult] {
      override def onNext(result: InsertOneResult): Unit =
        println(s"Inserted: $result")

      override def onError(e: Throwable): Unit =
        println(s"Failed: $e")

      override def onComplete(): Unit =
        println("Completed")
    })

  private def updateOne(updateObs: SingleObservable[UpdateResult]): Unit =
    updateObs.subscribe(new Observer[UpdateResult] {
      override def onNext(result: UpdateResult): Unit =
        println(s"Updated: $result")

      override def onError(e: Throwable): Unit =
        println(s"Failed: $e")

      override def onComplete(): Unit =
        println("Completed")
    })

  private def deleteOne(updateObs: SingleObservable[DeleteResult]): Unit =
    updateObs.subscribe(new Observer[DeleteResult] {
      override def onNext(result: DeleteResult): Unit =
        println(s"Updated: $result")

      override def onError(e: Throwable): Unit =
        println(s"Failed: $e")

      override def onComplete(): Unit =
        println("Completed")
    })

  def queryPlayer(playerDoc: BsonDocument): String =
    val name = playerDoc.get("name").asString().getValue
    val karten = playerDoc.get("cards").asString().getValue
    val kartenzahl = playerDoc.get("card_count").asInt32().getValue
    val placed = playerDoc.get("placed").asBoolean().getValue
    s"""{"name" : "$name", "karten" : $karten, "kartenzahl" : $kartenzahl, "placed" : $placed}"""
}
