package controller.controllerComponent

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCode}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.model.Uri
import scala.concurrent.Await
import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import akka.http.scaladsl.model.Uri
import scala.util.{Failure, Success, Try}
import play.api.libs.json.*
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.duration.*
import fileIOComponent.JSONImpl.fileIO
import model.gameComponent.gameInterface
import model.gameComponent.gameBaseImpl.{Game, Player, UnoState}
import controller.controllerComponent.WebClient

import scala.concurrent.ExecutionContext.Implicits.global


class PersistenceRequest {

  val fio = new fileIO()
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: Materializer = SystemMaterializer(system).materializer

  val port: String = sys.env.getOrElse("PERSISTENCE_SERVICE_PORT", "8081")
  val path: String = sys.env.getOrElse("PERSISTENCE_SERVICE_PATH", "/persistence/")
  val host: String = sys.env.getOrElse("PERSISTENCE_SERVICE_HOST", "uno-persistence-service")

  val webClient = new WebClient(s"http://$host:$port$path")

  def loadGame(result: Future[HttpResponse]): String = {
    var resJSON = ""
    val res = result.flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].map { jsonStr =>
            resJSON = jsonStr
          }
        case _ =>
          Future.failed(new RuntimeException(s"HTTP request failed with status ${response.status} and entity ${response.entity}"))
      }
    }
    Await.result(res, 10.seconds)
    resJSON
  }


  def save(game: gameInterface): Unit = {
    val endpoint = "dbstore"
    val putResponse = webClient.putRequest(fio.gameToJson(game).toString(), endpoint)
    val res = putResponse.flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].map { entity =>
            println(s"Request completed successfully with status ${response.status} and content:\n${entity}")
          }
        case _ =>
          Future.failed(new RuntimeException(s"HTTP request failed with status ${response.status} and entity ${response.entity}"))
      }
    }
    Await.result(res, 10.seconds)
  }

  def load(id : Option[Int] = None): String = {
    val endpoint = id match {
      case Some(id) => s"dbload/?id=$id"
      case None => "dbload"
    }
    val postResponse = webClient.getRequest(endpoint)
    loadGame(postResponse)
  }
}