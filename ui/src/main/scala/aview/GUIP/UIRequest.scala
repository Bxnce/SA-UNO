package aview.GUIP

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCode}
import akka.http.scaladsl.server.{ExceptionHandler, Route}

import scala.util.{Failure, Success, Try}
import play.api.libs.json.*
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.duration.*
import fileIOComponent.JSONImpl.fileIO
import model.gameComponent.gameInterface
import akka.stream.ActorMaterializer
import model.gameComponent.gameBaseImpl.{Game, Player, UnoState}


class UIRequest {

  val fileIO = new fileIO
  var game: gameInterface = new Game("place_h", "place_h", UnoState.between21State).init()


  def fetchData(apiEndpoint: String): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "SingleRequest")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val uri = "http://localhost:8080/controller/" + apiEndpoint
    println(uri)
    val responseFuture = Http().singleRequest(HttpRequest(uri=uri))
    responseFuture
      .onComplete {
        case Failure(_) => sys.error("Failed getting Json")
        case Success(value) => {
          Unmarshal(value.entity).to[String].onComplete {
            case Failure(_) => sys.error("Failed unmarshalling")
            case Success(value) => {
              this.game = fileIO.jsonToGame(value)
            }
          }
        }
      }
  }

  def fetchGame(): Unit = {
    val endpoint = "get"
    fetchData(endpoint)
  }

  def undo(): Unit = {
    val endpoint = "undo"
    fetchData(endpoint)
  }

  def redo(): Unit = {
    val endpoint = "redo"
    fetchData(endpoint)
  }

  def load(): Unit = {
    val endpoint = "load"
    fetchData(endpoint)
  }

  def save(): Unit = {
    val endpoint = "save"
    fetchData(endpoint)
  }

  def newG(name1: String, name2: String): Unit = {
    val endpoint = s"newg?name1=$name1&name2=$name2"
    fetchData(endpoint)
  }

  def WinG(name1: String, name2: String): Unit = {
    val endpoint = s"wing?name1=$name1&name2=$name2"
    fetchData(endpoint)
  }

  def place(ind: Int): Unit = {
    val endpoint = s"place?ind=$ind"
    fetchData(endpoint)
  }

  def take(): Unit = {
    val endpoint = s"take"
    fetchData(endpoint)
  }

  def next(): Unit = {
    val endpoint = s"next"
    fetchData(endpoint)
  }

  def colorChoose(color: String): Unit = {
    val endpoint = s"color?color=$color"
    fetchData(endpoint)
  }

  def request(req: String): Unit = {
    val endpoint = "request/" + req
    fetchData(endpoint)
  }
}