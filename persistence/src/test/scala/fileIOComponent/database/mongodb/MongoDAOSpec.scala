package fileIOComponent.database.mongodb

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter
import com.google.inject.Inject
import fileIOComponent.JSONImpl.fileIO
import fileIOComponent.database.mongodb.{getHighestId, handleResult}
import fileIOComponent.database.{DAOInterface, WAIT_TIME}
import model.gameComponent.gameBaseImpl.{Game, UnoState}
import model.gameComponent.gameInterface
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.*
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters.{equal, *}
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable, result, SingleObservableFuture}

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

class MongoDAOSpec extends AnyWordSpec with Matchers with BeforeAndAfter {
  private val database_pw = sys.env.getOrElse("MONGO_ROOT_PASSWORD", "mongo")
  private val database_username = sys.env.getOrElse("MONGO_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_HOST", "localhost")
  private val port = sys.env.getOrElse("MONGO_PORT", "27017")

  val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"

  private var client: MongoClient = _
  private var db: MongoDatabase = _
  private var collection: MongoCollection[Document] = _
  
  private var simpleMongo: SimpleMongoDAO = _
  val fileIO = new fileIO
  val basic_game: Game = new Game("Bence", "Timo", UnoState.between21State).init()

  before {
    client = MongoClient(uri)
    db = client.getDatabase("test_db")
    collection = db.getCollection("test_collection")
    simpleMongo = new SimpleMongoDAO()
    simpleMongo.gameCollection = collection
  }

  after {
    Await.result(collection.drop().toFuture(), 5.seconds)
    client.close()
  }

  "SimpleMongoDAO" should {
    "be able to save a json game string in the collection" in {
      simpleMongo.save(basic_game)

      val result = Await.result(collection.find().first().head(), 5.second)
      result("game").asString().getValue shouldEqual fileIO.gameToJson(basic_game).toString()
    }

    "be able to load a game from the collection" in {
      simpleMongo.save(basic_game)

      val res = simpleMongo.load()
      res.isSuccess shouldEqual true
      val loadedGame = res.get
      loadedGame.pList shouldEqual basic_game.pList
      loadedGame.currentstate shouldEqual basic_game.currentstate
    }

    "be able to delete a game in the database" in {
      val gameId = 123

      handleResult(collection.insertOne(Document("_id" -> gameId, "game" -> fileIO.gameToJson(basic_game).toString())))

      val res = simpleMongo.deleteGame(gameId)

      res.isSuccess shouldBe true
    }

    "be able to update a game in the database" in {
      val gameId = 123

      handleResult(collection.insertOne(Document("_id" -> gameId, "game" -> fileIO.gameToJson(basic_game).toString())))

      val res = simpleMongo.updateGame(gameId, currentstate = Some("player1state"))

      val updatedGame = simpleMongo.load(Some(gameId))
      updatedGame.get.currentstate shouldEqual UnoState.between21State
    }
  }
}
