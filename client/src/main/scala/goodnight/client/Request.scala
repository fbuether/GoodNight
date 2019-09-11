
package goodnight.client

import java.io.IOException

import scala.concurrent.Future
import scala.util.{Failure, Success}

import org.scalajs.dom.document
import org.scalajs.dom.window

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method._
import fr.hmil.roshttp.body.PlainTextBody
import fr.hmil.roshttp.util.HeaderMap
import fr.hmil.roshttp.response.SimpleHttpResponse
import fr.hmil.roshttp.body.Implicits._
import fr.hmil.roshttp.exceptions.HttpException
import monix.execution.Scheduler.Implicits.global

import play.api.libs.json._
import japgolly.scalajs.react._


case class Reply(statusCode: Int, body: JsValue)

class Request(req: HttpRequest) {

  def withBody(body: JsValue) =
    Request(req.
      withBody(PlainTextBody(Json.stringify(body))).
      withHeader("Content-Type", "application/json"))

  private val authStore = "authentication"
  private val authHeader = "X-Auth-Token"

  private def storeAuthentication(headers: HeaderMap[String]) = {
    // AsyncCallback[Unit] = {
    if (headers.contains(authHeader))
      window.localStorage.setItem(authStore, headers(authHeader))
  }

  private def attachAuthentication(req: HttpRequest): HttpRequest = {
    window.localStorage.getItem(authStore) match {
      case "" => req
      case token => req.withHeader("Authorization", "Bearer " + token)
    }
  }



  private def performRequest: AsyncCallback[(Int, String)] =
    AsyncCallback.fromFuture(
      attachAuthentication(req).
      send.
      map({ r =>
        println("got reply: " + r.statusCode + " -> " + r.body)
        storeAuthentication(r.headers)
        (r.statusCode, r.body) }).
      recoverWith({
        case (e: HttpException[SimpleHttpResponse]) =>
          println("got reply: " + e.response.statusCode + " -> " +
            e.response.body)
          storeAuthentication(e.response.headers)
          Future.successful((e.response.statusCode, e.response.body))
        case (e: IOException) =>
          println("unexpected http exception: " +
            e.getClass() + ", " +
            e.getMessage() + ", ")
          Future.successful((0, ""))
      }))

  def send: AsyncCallback[Reply] =
    performRequest.map({ case (status, body) =>
      if (body.isEmpty) Reply(status, JsNull)
      else Reply(status, Json.parse(body))
    })
}


object Request {
  private val baseUrl = "http://localhost:9000"

  def apply(request: HttpRequest) =
    new Request(request)

  def get(url: String): Request =
    Request(HttpRequest(baseUrl + url).
      withMethod(GET).
      withHeader("Accept", "text/json"))

  def post(url: String): Request =
    Request(HttpRequest(baseUrl + url).
      withMethod(POST).
      withHeader("Accept", "text/json"))

}
