/**
 * RootSevice.scala
 *  implementation for AKKA RestControllerAPI
 */

//****************************************************************************** PACKAGE
package fileIOComponent

//****************************************************************************** IMPORTS

import fileIOComponent.JSONImpl.fileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import play.api.libs.json.*

//****************************************************************************** CLASS DEFINITION
class RestAPIPersistence():

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val fileIO = new fileIO
  val RestUIPort = 8081
  val routes: String =
    """
        """.stripMargin

  val route: Route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      get {
        path("persistence"/ "load") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.load.toString))
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
      }
    )

  def start(): Unit = {
    val binding = Http().newServerAt("localhost", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) => {
        println(s"UNO PersistenceAPI service online at http://localhost:$RestUIPort/")
      }
      case Failure(exception) => {
        println(s"UNO PersistenceAPI service failed to start: ${exception.getMessage}")
      }
    }
  }