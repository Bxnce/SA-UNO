package fileIOComponent

/* Uno-Dependencies*/

import fileIOComponent.JSONImpl.fileIO
import fileIOComponent.PersistenceModule
import fileIOComponent.database.DAOInterface
import model.gameComponent.gameBaseImpl.{Game, UnoState}
import model.gameComponent.gameInterface

/*Libaries*/
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import akka.stream.ActorMaterializer
import com.google.inject.{Guice, Inject, Injector}
import play.api.libs.json.*

import scala.concurrent.{ExecutionContextExecutor, Future}
import concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class RestAPIPersistence():
  private val injector: Injector = Guice.createInjector(new PersistenceModule())
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val fileIO = new fileIO
  val database: DAOInterface = injector.getInstance(classOf[DAOInterface])

  val RestUIPort: Int = sys.env.getOrElse("PERSISTENCE_SERVICE_PORT", "8081").toInt
  val RestUIHost: String = sys.env.getOrElse("PERSISTENCE_SERVICE_HOST", "localhost")

  val routes: String =
    """
        """.stripMargin

  val route: Route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      get {
        path("persistence" / "load") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(fileIO.load).toString()))
        }
      },
      get {
        path("persistence" / "dbload") {
          parameter("id".?) { id =>
            val idUpdated = id.map(_.toInt)

            val future = database.load(idUpdated)
            onComplete(future) {
              case Success(game: gameInterface) =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(game).toString()))
              case Failure(_) =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(new Game("ERROR LOADING DATABASE", "ERROR LOADING DATABASE", UnoState.winState)).toString()))
            }
          }
        }
      },
      put {
        path("persistence" / "store") {
          entity(as[String]) { data =>
            complete {
              fileIO.save(fileIO.jsonToGame(data))
              Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, "game successfully saved"))
            }
          }
        }
      },
      put {
        path("persistence" / "dbstore") {
          entity(as[String]) { data =>
            complete {
              database.save(fileIO.jsonToGame(data))
              Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, "game successfully saved"))
            }
          }
        }
      },
      put {
        path("persistence" / "dbupdateplayer") {
          parameter("id".as[Int], "name".?, "cards".?, "card_count".as[Int].?, "placed".as[Boolean].?) {
            (id, name, cards, card_count, placed) =>
              val cardCountUpdated = card_count
              val placedUpdated = placed
              val resultFuture = database.updatePlayer(id, name, cards, cardCountUpdated, placedUpdated)
                .map(result => s"updated player database $result")
                .recover { case _ => "ERROR" }

              onComplete(resultFuture) { result =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, result.getOrElse("ERROR")))
              }
          }
        }
      },
      put {
        path("persistence" / "dbupdategame") {
          parameter("id".as[Int], "player1".as[Int].?, "player2".as[Int].?, "midcard".as[Int].?,
            "currentstate".?, "error".as[Int].?, "cardstack".?, "winner".as[Int].?) {
            (id, player1, player2, midcard, currentstate, error, cardstack, winner) =>
              val player1Updated = player1
              val player2Updated = player2
              val midcardUpdated = midcard
              val errorUpdated = error
              val winnerUpdated = winner
              val resultFuture = database.updateGame(id, player1Updated, player2Updated, midcardUpdated,
                currentstate, errorUpdated, cardstack, winnerUpdated)
                .map(result => s"updated game database $result")
                .recover { case _ => "ERROR" }

              onComplete(resultFuture) { result =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, result.getOrElse("ERROR")))
              }
          }
        }
      },
      put {
        path("persistence" / "dbdeleteplayer") {
          parameter("id".as[Int]) { id =>
            val resultFuture = database.deletePlayer(id)
              .map(result => s"deleted player in player database $result")
              .recover { case _ => "ERROR" }

            onComplete(resultFuture) { result =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, result.getOrElse("ERROR")))
            }
          }
        }
      },
      put {
        path("persistence" / "dbdeletegame") {
          parameter("id".as[Int]) { id =>
            val resultFuture = database.deleteGame(id)
              .map(result => s"deleted game in game database $result")
              .recover { case _ => "ERROR" }

            onComplete(resultFuture) { result =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, result.getOrElse("ERROR")))
            }
          }
        }
      }
      ,
    )

  def start(): Unit = {
    val binding = Http().newServerAt(RestUIHost, RestUIPort).bind(route)

    binding.onComplete {
      case Success(_) =>
        println(s"UNO PersistenceAPI service online at http://$RestUIHost:$RestUIPort/")
      case Failure(exception) =>
        println(s"UNO PersistenceAPI service failed to start: ${exception.getMessage}")
    }
  }