package fileIOComponent.database.mongodb

import com.google.inject.Inject
import fileIOComponent.JSONImpl.fileIO
import fileIOComponent.database.mongodb.{getHighestId, handleResult}
import fileIOComponent.database.{DAOInterface, FutureHandler, WAIT_TIME}
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
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/*
Simple implementation of a MongoDB DAO - makes more sense, as everything in the database is a JSON anyway
*/
class SimpleMongoDAO @Inject() extends DAOInterface {
  private val fio = new fileIO
  private val future_handler = new FutureHandler()
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
  println("Connected to MongoDB")

  override def save(game: gameInterface): Future[Unit] =
    val future = Future {
      handleResult(gameCollection.insertOne(Document(
        "_id" -> (getHighestId(gameCollection) + 1),
        "game" -> fio.gameToJson(game).toString()
      )))
    }
    future_handler.resolveNonBlockingOnFuture(future)


  override def load(id: Option[Int] = None): Future[Try[gameInterface]] =
    val future = Future {
      Try {
        Await.result(gameCollection.find(equal("_id", id.getOrElse(getHighestId(gameCollection)))).first().head(), WAIT_TIME).get("game") match {
          case Some(value) => fio.jsonToGame(value.asString().getValue)
          case None => throw new Exception("Game not found")
        }
      }
    }
    future_handler.resolveNonBlockingOnFuture(future)

  override def updateGame(id: Int, player1: Option[Int] = None, player2: Option[Int] = None, midCard: Option[Int] = None, currentstate: Option[String] = None, error: Option[Int] = None, cardstack: Option[String] = None, winner: Option[Int] = None): Future[Try[Boolean]] =
    val future = Future {
      Try {
        var gameJson = Json.parse(Await.result(gameCollection.find(equal("_id", id)).first().head(), WAIT_TIME)("game").asString().getValue)
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
    }
    future_handler.resolveNonBlockingOnFuture(future)

  override def deleteGame(id: Int): Future[Try[Boolean]] =
    val future = Future {
      Try {
        handleResult(gameCollection.deleteOne(equal("_id", id)))
        true
      }
    }
    future_handler.resolveNonBlockingOnFuture(future)

  override def updatePlayer(id: Int, name: Option[String], cards: Option[String], card_count: Option[Int], placed: Option[Boolean]): Future[Try[Boolean]] =
    ???

  override def deletePlayer(id: Int): Future[Try[Boolean]] =
    ???

  private def handleResult[T](obs: SingleObservable[T]): Unit =
    Await.result(obs.asInstanceOf[SingleObservable[Unit]].head(), WAIT_TIME)
}
