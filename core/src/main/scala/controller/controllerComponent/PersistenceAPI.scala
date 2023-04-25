package controller.controllerComponent

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PersistenceAPI {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      implicit val system: ActorSystem[Nothing] = context.system
      implicit val materializer: Materializer = Materializer(context)
      implicit val ec: ExecutionContext = context.system.executionContext

      val url = "http://localhost:8081/persistence"

      def sendPutRequest(data: String): Future[String] = {
        val request = HttpRequest(
          method = HttpMethods.PUT,
          uri = url + "/store",
          entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, data)
        )

        val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

        responseFuture.flatMap { response =>
          response.status match {
            case StatusCodes.OK =>
              Unmarshal(response.entity).to[String].map { responseBody =>
                responseBody
              }
            case _ =>
              response.discardEntityBytes()
              Future.failed(new RuntimeException(s"Unexpected status code ${response.status}"))
          }
        }
      }

      def sendGetRequest(): Future[String] = {
        val request = HttpRequest(
          method = HttpMethods.GET,
          uri = url + "/load"
        )

        val responseFuture: Future[HttpResponse] = Http().singleRequest(request)

        responseFuture.flatMap { response =>
          response.status match {
            case StatusCodes.OK =>
              Unmarshal(response.entity).to[String].map { responseBody =>
                responseBody
              }
            case _ =>
              response.discardEntityBytes()
              Future.failed(new RuntimeException(s"Unexpected status code ${response.status}"))
          }
        }
      }

      val inputData = "This is some data to send via PUT."

      sendPutRequest(inputData).onComplete {
        case Success(responseBody) =>
          println(s"Response received from PUT request: $responseBody")
        case Failure(ex) =>
          println(s"PUT request failed: ${ex.getMessage}")
      }

      sendGetRequest().onComplete {
        case Success(responseBody) =>
          println(s"Response received from GET request: $responseBody")
        case Failure(ex) =>
          println(s"GET request failed: ${ex.getMessage}")
      }

      Behaviors.empty
    }
    ActorSystem[Nothing](rootBehavior, "Example")
}