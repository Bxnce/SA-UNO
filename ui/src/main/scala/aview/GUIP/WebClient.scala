package aview.GUIP

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.Materializer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WebClient(serverUri: String)(implicit system: ActorSystem, mat: Materializer) {

  private val http = Http()

  def putRequest(data: String, path: String): Future[HttpResponse] = {
    val request = HttpRequest(
      method = HttpMethods.PUT,
      uri = serverUri + path,
      entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, data)
    )
    print("Put " + request.uri)
    http.singleRequest(request)
  }

  def getRequest(path: String): Future[HttpResponse] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = serverUri + path
    )
    print("Get " + request.uri)
    http.singleRequest(request)
  }

  def postRequest(data: String, path: String): Future[HttpResponse] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = serverUri + path,
      entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, data)
    )
    print("Post " + request.uri)
    http.singleRequest(request)
  }
}