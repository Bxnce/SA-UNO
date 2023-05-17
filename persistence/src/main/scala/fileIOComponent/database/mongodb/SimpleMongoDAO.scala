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
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

/*
Simple implementation of a MongoDB DAO - makes more sense, as everything in the database is a JSON anyway
*/
class SimpleMongoDAO @Inject() extends DAOInterface {
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
  private val gameCollection: MongoCollection[Document] = db.getCollection("game")
  println("Connected to MongoDB")

  override def save(game: gameInterface): Unit =
    println("Saving game to MongoDB")
    handleResult(gameCollection.insertOne(Document(
      "_id" -> (getHighestId(gameCollection) + 1),
      "game" -> fio.gameToJson(game).toString()
    )))
    println(s"Inserted game in MongoDB with id ${getHighestId(gameCollection)}")

  override def load(id: Option[Int]): Try[gameInterface] =
    Try {
      Await.result(gameCollection.find(equal("_id", id.getOrElse(getHighestId(gameCollection)))).first().head(), WAIT_TIME).get("game") match {
        case Some(value) => fio.jsonToGame(value.asString().getValue)
        case None => throw new Exception("Game not found")
      }
    }

  override def updateGame(id: Int, player1: Option[Int], player2: Option[Int], midCard: Option[Int], currentstate: Option[String], error: Option[Int], cardstack: Option[String], winner: Option[Int]): Try[Boolean] =
    Try {
      var gameJson = Json.parse(Await.result(gameCollection.find(equal("_id", id)).first().head(), WAIT_TIME).toString())
      currentstate match {
        case Some(value) => gameJson = gameJson.as[JsObject] ++ Json.obj("currentstate" -> value)
        case None =>
      }
      error match {
        case Some(value) => gameJson = gameJson.as[JsObject] ++ Json.obj("ERROR" -> value)
        case None =>
      }
      cardstack match {
        case Some(value) => gameJson = gameJson.as[JsObject] ++ Json.obj("cardstack" -> value)
        case None =>
      }
      winner match {
        case Some(value) => gameJson = gameJson.as[JsObject] ++ Json.obj("winner" -> value)
        case None =>
      }
      handleResult(gameCollection.updateOne(equal("_id", id), Updates.set("game", gameJson)))
      true
    }

  override def deleteGame(id: Int): Try[Boolean] =
    Try {
      handleResult(gameCollection.deleteOne(equal("_id", id)))
      true
    }

  override def updatePlayer(id: Int, name: Option[String], cards: Option[String], card_count: Option[Int], placed: Option[Boolean]): Try[Boolean] =
    throw new Exception("Not implemented")

  override def deletePlayer(id: Int): Try[Boolean] =
    throw new Exception("Not implemented")

  private def handleResult[T](obs: SingleObservable[T]): Unit =
    Await.result(obs.asInstanceOf[SingleObservable[Unit]].head(), WAIT_TIME)
    println("db operation successful")
}
