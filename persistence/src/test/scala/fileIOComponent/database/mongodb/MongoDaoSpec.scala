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
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, ObservableFuture, Observer, SingleObservable, SingleObservableFuture, result}

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try
class MongoDaoSpec extends AnyWordSpec with Matchers with BeforeAndAfter{

  private val database_pw = sys.env.getOrElse("MONGO_ROOT_PASSWORD", "mongo")
  private val database_username = sys.env.getOrElse("MONGO_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_HOST", "localhost")
  private val port = sys.env.getOrElse("MONGO_PORT", "27017")

  val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"

  private var client: MongoClient = _
  private var db: MongoDatabase = _
  private var game_collection: MongoCollection[Document] = _
  private var player_collection: MongoCollection[Document] = _

  private var mongoDAO: MongoDAO = _

  val fileIO = new fileIO
  val basic_game: Game = new Game("Bence", "Timo", UnoState.between21State).init()

  before {
    client = MongoClient(uri)
    db = client.getDatabase("test_db")
    game_collection = db.getCollection("test_game_collection")
    player_collection = db.getCollection("test_player_collection")
    mongoDAO = new MongoDAO()
    mongoDAO.gameCollection = game_collection
    mongoDAO.playerCollection = player_collection
  }

  after {
    Await.result(game_collection.drop().toFuture(), 5.seconds)
    Await.result(player_collection.drop().toFuture(), 5.seconds)
    client.close()
  }

  "SimpleMongoDAO" should {
    "be able to save a json game string in the collection" in {
      mongoDAO.save(basic_game)

      val gameDocument = Await.result(game_collection.find().first().head(), WAIT_TIME)
      gameDocument should not be null

      val players = Await.result(player_collection.find().toFuture(), WAIT_TIME)
      players.length shouldBe 3
    }

    "load a game from the database" in {
      mongoDAO.save(basic_game)
      val gameId = getHighestId(game_collection)
      val loadedGame = mongoDAO.load(Some(gameId))
      loadedGame.get.pList shouldBe basic_game.pList
      loadedGame.get.currentstate shouldBe basic_game.currentstate
    }

    "delete a game from the database" in {
      mongoDAO.save(basic_game)
      val gameId = getHighestId(game_collection)

      val res = mongoDAO.deleteGame(gameId)

      res.isSuccess shouldBe true

      val games = Await.result(game_collection.find().toFuture(), WAIT_TIME)
      games.length shouldBe 0
    }

    "delete a player from the database" in {
      mongoDAO.save(basic_game)

      val playerId = getHighestId(player_collection)
      val res = mongoDAO.deletePlayer(playerId)

      res.isSuccess shouldBe true

      val players = Await.result(player_collection.find().toFuture(), WAIT_TIME)
      players.length shouldBe 2
    }

    "update a game in the database" in {
      mongoDAO.save(basic_game)
      val gameId = getHighestId(game_collection)

      mongoDAO.updateGame(gameId, error = Some(1))
      val loadedGame = mongoDAO.load(Some(gameId))
      loadedGame.get.ERROR shouldBe 0
    }

    "update a player in the database" in {
      mongoDAO.save(basic_game)
      val playerId = getHighestId(player_collection)
      val updatedName = "Updated Player"
      mongoDAO.updatePlayer(0, placed = Some(true))
      val loadedPlayer = Await.result(game_collection.find().first().head(), WAIT_TIME)
    }

  }

}
