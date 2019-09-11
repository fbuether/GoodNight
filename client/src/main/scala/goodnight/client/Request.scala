
package goodnight.client

import java.io.IOException

import scala.concurrent.Future
import scala.util.{Failure, Success}

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method._
import fr.hmil.roshttp.body.PlainTextBody
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

  private def performRequest: AsyncCallback[(Int, String)] =
    AsyncCallback.fromFuture(req.send.
      map({ r =>
        println("got reply: " + r.statusCode + " -> " + r.body)
        (r.statusCode, r.body) }).
      recoverWith({
        case (e: HttpException[SimpleHttpResponse]) =>
          println("got reply: " + e.response.statusCode + " -> " +
            e.response.body)
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
