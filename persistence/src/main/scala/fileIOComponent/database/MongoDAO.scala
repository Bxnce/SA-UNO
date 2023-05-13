package fileIOComponent.database

import fileIOComponent.JSONImpl.fileIO
import fileIOComponent.database.{DAOInterface, WAIT_TIME}
import model.gameComponent.gameInterface
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.model.{Aggregates, FindOneAndUpdateOptions, ReturnDocument, Sorts}
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable, result}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try


class MongoDAO extends DAOInterface {
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
    ???


  override def storePlayer(name: String, cards: String, card_count: Int, placed: Boolean): Int = ???

  override def storeGame(player1: Int, player2: Int, midCard: Int, currentstate: String, error: Int, cardstack: String, winner: Int): Int = ???

  override def updateGame(id: Int, player1: Option[Int], player2: Option[Int], midCard: Option[Int], currentstate: Option[String], error: Option[Int], cardstack: Option[String], winner: Option[Int]): Try[Boolean] = ???

  override def updatePlayer(id: Int, name: Option[String], cards: Option[String], card_count: Option[Int], placed: Option[Boolean]): Try[Boolean] = ???

  override def deleteGame(id: Int): Try[Boolean] = ???

  override def deletePlayer(id: Int): Try[Boolean] = ???

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

}