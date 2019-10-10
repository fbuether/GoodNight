
package goodnight.service

import java.io.IOException

import scala.concurrent.Future
import scala.util.{Try, Failure, Success}

import org.scalajs.dom.document

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

import goodnight.common.ApiV1

case class Reply[T](statusCode: Int, body: T)


class Request(req: HttpRequest) {
  private val authHeader = "X-Auth-Token"

  private def storeAuthentication(headers: HeaderMap[String]): Callback =
    headers.get(authHeader).
      map(AuthenticationService.setAuthentication).
      getOrElse(Callback(()))

  private def attachAuthentication(req: HttpRequest): CallbackTo[HttpRequest] =
    AuthenticationService.getAuthentication.
      map(t => t.map(t => req.withHeader(authHeader, t)).
        getOrElse(req))

  private def successResult(r: SimpleHttpResponse):
      CallbackTo[(Int, String)] = {
    Callback { println(s"reply: ${r.statusCode} -> ${r.body}") } >>
    storeAuthentication(r.headers) >>
    CallbackTo((r.statusCode, r.body))
  }

  private def catchErrors(request: Try[SimpleHttpResponse]):
  Try[SimpleHttpResponse] = {
    request match {
      case Success(r) => Success(r)
      case Failure(e) => e match {
        case (e: HttpException[SimpleHttpResponse]) => Success(e.response)
        case _ => Failure(e)
      }
    }
  }

  private def treatResult(request: Future[SimpleHttpResponse]):
      AsyncCallback[(Int, String)] =
    AsyncCallback.
      fromFuture(request.transform(catchErrors)).
      flatMap(r => successResult(r).asAsyncCallback)

  private def performRequest: AsyncCallback[(Int, String)] =
    attachAuthentication(req).map(_.send).
      asAsyncCallback.
      flatMap(treatResult)



  def withBody(body: JsValue) =
    Request(req.
      withBody(PlainTextBody(Json.stringify(body))).
      withHeader("Content-Type", "application/json"))

  def send: AsyncCallback[Reply[String]] =
    performRequest.map({ case (status, body) => Reply(status, body) })
}


object Conversions {
  implicit class OfReply(reply: AsyncCallback[Reply[String]]) {
    def forJson: AsyncCallback[Reply[Try[JsValue]]] =
      reply.map({ case Reply(code, body) =>
        Reply(code,
          if (body.isEmpty) Success(JsNull)
          else Try(Json.parse(body)))
      })
  }
}


object Request {
  private val baseUrl = "http://localhost:9000"

  def apply(request: HttpRequest) =
    new Request(request)

  def get(url: String): Request =
    Request(HttpRequest(baseUrl + url).
      withMethod(GET).
      withHeader("Accept", "application/json"))

  def post(url: String): Request =
    Request(HttpRequest(baseUrl + url).
      withMethod(POST).
      withHeader("Accept", "application/json"))

  def put(url: String): Request =
    Request(HttpRequest(baseUrl + url).
      withMethod(PUT).
      withHeader("Accept", "application/json"))

  // todo: fixme, wrong path.
  def put(path: ApiV1.ApiPath, params: String*): Request =
    Request(HttpRequest(baseUrl + path.write(params : _*)).
      withMethod(PUT).
      withHeader("Accept", "application/json"))
}
