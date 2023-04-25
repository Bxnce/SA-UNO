package aview.GUIP

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
import aview.GUIP.WebClient
import controller.controllerComponent.Observable

import scala.concurrent.ExecutionContext.Implicits.global


class UIRequest extends Observable{

  val fio = new fileIO()
  var game: gameInterface = new Game("place_h", "place_h", UnoState.between21State).init()
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: Materializer = SystemMaterializer(system).materializer

  //implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
  //implicit val executionContext = system.executionContext

  val webClient = new WebClient("http://localhost:8080/controller/")

  def waitRefreshGame(resulti: Future[HttpResponse]) = {
    val res = resulti .flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].map { jsonStr =>
            this.game = fio.jsonToGame(jsonStr)
          }
        case _ =>
          Future.failed(new RuntimeException(s"HTTP request failed with status ${response.status} and entity ${response.entity}"))
      }
    }
    Await.result(res, 10.seconds)
    notifyObservers
  }

  def undo(): Unit = {
    val endpoint = "undo"
    val postResponse = webClient.getRequest(endpoint)
    waitRefreshGame(postResponse)
  }

  def redo(): Unit = {
    val endpoint = "redo"
    val postResponse = webClient.getRequest(endpoint)
    waitRefreshGame(postResponse)
  }

  def load(): Unit = {
    val endpoint = "load"
    val postResponse = webClient.getRequest(endpoint)
    waitRefreshGame(postResponse)
  }

  def save(): Unit = {
    val endpoint = "save"
    val postResponse = webClient.postRequest(fio.gameToJson(this.game).toString(), endpoint)
    waitRefreshGame(postResponse)
  }

  def newG(name1: String, name2: String): Unit = {
    val endpoint = s"newg?name1=$name1&name2=$name2"
    val postResponse = webClient.postRequest("", endpoint)
    waitRefreshGame(postResponse)
    }


  def WinG(name1: String, name2: String): Unit = {
    val endpoint = s"wing?name1=$name1&name2=$name2"
    val postResponse = webClient.postRequest("",endpoint)
    waitRefreshGame(postResponse)
  }

  def place(ind: Int): Unit = {
    val endpoint = s"place?ind=$ind"
    val postResponse = webClient.postRequest("",endpoint)
    waitRefreshGame(postResponse)
  }

  def take(): Unit = {
    val endpoint = s"take"
    val postResponse = webClient.postRequest("",endpoint)
    waitRefreshGame(postResponse)
  }

  def next(): Unit = {
    val endpoint = s"next"
    val postResponse = webClient.postRequest("",endpoint)
    waitRefreshGame(postResponse)
  }

  def colorChoose(color: String): Unit = {
    val endpoint = s"color?color=$color"
    val postResponse = webClient.postRequest("",endpoint)
    waitRefreshGame(postResponse)
  }
}