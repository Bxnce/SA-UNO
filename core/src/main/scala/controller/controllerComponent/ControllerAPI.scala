package controller.controllerComponent

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
import controller.controllerComponent.controllerInterface
import model.gameComponent.gameInterface

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import play.api.libs.json.*


class ControllerAPI(using controller: controllerInterface):

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val fileIO = new fileIO
  val RestUIPort = 8080
  val routes: String =
    """
        """.stripMargin

  val route: Route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      get {
        path("controller" / "get") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(controller.game).toString()))
        }
      },
      get {
        path("controller" / "redo") {
          controller.redo()
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(controller.game).toString()))
        }
      },
      get {
        controller.undo()
        path("controller" / "undo") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(controller.game).toString()))
        }
      },
      get {
        path("controller" / "load") {
          controller.load()
          complete(HttpEntity(ContentTypes.`application/json`, fileIO.gameToJson(controller.game).toString()))
        }
      },
      post {
        path("controller" / "save") {
          controller.save()
          complete(HttpEntity(ContentTypes.`application/json`, fileIO.gameToJson(controller.game).toString()))
        }
      },
      post {
        path("controller" / "newg") {
          parameter("name1", "name2") { (name1, name2) =>
            controller.newG(name1, name2)
            complete(HttpEntity(ContentTypes.`application/json`, fileIO.gameToJson(controller.game).toString()))
          }
        }
      },
      post {
        path("controller" / "take") {
          controller.take()
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(controller.game).toString()))
        }
      },
      post {
        path("controller" / "place") {
          parameter("ind") { (ind) =>
            controller.place(ind.toInt)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(controller.game).toString()))
          }
        }
      },
      post {
        path("controller" / "next") {
          controller.next()
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(controller.game).toString()))
        }
      },
      post {
        path("controller" / "wing") {
          parameter("name1", "name2") { (name1, name2) =>
            controller.WinG(name1, name2)
            complete(HttpEntity(ContentTypes.`application/json`, fileIO.gameToJson(controller.game).toString()))
          }
        }
      },
      post {
        path("controller" / "color") {
          parameter("color") { (color) =>
            controller.colorChoose(color)
            complete(HttpEntity(ContentTypes.`application/json`, fileIO.gameToJson(controller.game).toString()))
          }
        }
      },
    )

  def start(): Unit = {
    val binding = Http().newServerAt("localhost", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) => {
        println(s"UNO ControllerAPI service online at http://localhost:$RestUIPort/")
      }
      case Failure(exception) => {
        println(s"UNO ControllerAPI service failed to start: ${exception.getMessage}")
      }
    }
  }