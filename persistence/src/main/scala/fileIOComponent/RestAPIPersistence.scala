package fileIOComponent

/* Uno-Dependencies*/

import fileIOComponent.JSONImpl.fileIO
import fileIOComponent.PersistenceModule
import fileIOComponent.database.DAOInterface
import model.gameComponent.gameBaseImpl.{Game, UnoState}

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
      get{
        concat(
          pathSingleSlash {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
          },
          path("persistence" / "load") {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(fileIO.load).toString()))
          },
          path("persistence" / "dbload") {
            parameter("id".?) { id =>
              val id_updated =
                id match {
                  case Some(id) => Some(id.toInt)
                  case None => None
                }
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(database.load(id_updated)
                .getOrElse(new Game("ERROR LOADING DATABASE", "ERROR LOADING DATABASE", UnoState.winState))).toString()))
            }
          },
        )
      },
      put{
        concat(
          path("persistence" / "store") {
            entity(as[String]) { data =>
              complete {
                fileIO.save(fileIO.jsonToGame(data))
                Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, "game successfully saved"))
              }
            }
          },
          path("persistence" / "dbstore") {
            entity(as[String]) { data =>
              complete {
                database.save(fileIO.jsonToGame(data))
                Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, "game successfully saved"))
              }
            }
          },
          path("persistence" / "dbupdateplayer") {
            parameter("id", "name".?, "cards".?, "card_count".?, "placed".?) {
              (id, name, cards, card_count, placed) =>
                complete {
                  val card_count_updated =
                    card_count match {
                      case Some(card_count) => Some(card_count.toInt)
                      case None => None
                    }
                  val placed_updated =
                    placed match {
                      case Some(placed) => Some(placed.toBoolean)
                      case None => None
                    }
                  val result = database.updatePlayer(id = id.toInt, name = name, cards = cards, card_count = card_count_updated,
                    placed = placed_updated).getOrElse("ERROR")
                  Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"updated player database" +
                    s" $result"))
                }
            }
          },
          path("persistence" / "dbupdategame") {
            parameter("id", "player1".?, "player2".?, "midcard".?, "currentstate".?, "error".?, "cardstack".?, "winner".?) {
              (id, player1, player2, midcard, currentstate, error, cardstack, winner) =>
                complete {
                  val player1_updated =
                    player1 match {
                      case Some(player1) => Some(player1.toInt)
                      case None => None
                    }
                  val player2_updated =
                    player2 match {
                      case Some(player2) => Some(player2.toInt)
                      case None => None
                    }
                  val midcard_updated =
                    midcard match {
                      case Some(midcard) => Some(midcard.toInt)
                      case None => None
                    }
                  val error_updated =
                    error match {
                      case Some(error) => Some(error.toInt)
                      case None => None
                    }
                  val winner_updated =
                    winner match {
                      case Some(winner) => Some(winner.toInt)
                      case None => None
                    }
                  val result = database.updateGame(id = id.toInt, player1 = player1_updated, player2 = player2_updated,
                    midCard = midcard_updated, currentstate = currentstate, error = error_updated, cardstack = cardstack,
                    winner = winner_updated).getOrElse("ERROR")
                  Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"updated game database" +
                    s" $result"))
                }
            }
          },
          path("persistence" / "dbdeleteplayer") {
            parameter("id") {
              id =>
                complete {
                  val result = database.deletePlayer(id = id.toInt).getOrElse("ERROR")
                  Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"deleted player in player database" +
                    s" $result"))
                }
            }
          },
          path("persistence" / "dbdeletegame") {
            parameter("id") {
              id =>
                complete {
                  val result = database.deleteGame(id = id.toInt).getOrElse("ERROR")
                  Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"deleted game in game database" +
                    s" $result"))
                }
            }
          },
        )
      }
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