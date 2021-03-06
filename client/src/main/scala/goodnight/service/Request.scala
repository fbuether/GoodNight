
package goodnight.service

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method
import fr.hmil.roshttp.body.Implicits._
import fr.hmil.roshttp.body.PlainTextBody
import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.SimpleHttpResponse
import fr.hmil.roshttp.util.HeaderMap
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.BaseUrl
import java.io.IOException
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom.document
import scala.concurrent.Future
import scala.util.{Try, Failure, Success}

import goodnight.common.ApiPath
import goodnight.common.ApiV1
import goodnight.common.Serialise._

case class Reply[T](statusCode: Int, body: T)


class Request(req: HttpRequest, authenticated: Boolean = true) {
  private val authHeader = "X-Auth-Token"

  private def storeAuthentication(headers: HeaderMap[String]): Callback =
    Callback({
      headers.get(authHeader).foreach({token => TokenStore.store(token) })
    })

  private def attachAuthentication(req: HttpRequest): CallbackTo[HttpRequest] =
    if (authenticated)
      CallbackTo(
        TokenStore.get.
          map(token => req.withHeader(authHeader, token)).
          getOrElse(req))
    else
      CallbackTo.pure(req)

  private def successResult(r: SimpleHttpResponse): CallbackTo[(Int, String)] =
    storeAuthentication(r.headers) >>
    CallbackTo((r.statusCode, r.body))

  private def catchErrors(request: Try[SimpleHttpResponse]):
  Try[SimpleHttpResponse] = {
    request match {
      case Success(r) => Success(r)
      case Failure(e) => e match {
        case (e: HttpException[_]) => e.response match {
          case r: SimpleHttpResponse => Success(r)
          case _ => Failure(e)
        }
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


  def query(field: String, value: String = "") =
    Request(req.
      withQueryParameter(field, value))

  def withBody[A](body: A)(implicit ev: Serialisable[A]) =
    Request(req.
      withBody(PlainTextBody(write(body))).
      withHeader("Content-Type", "application/json"))

  def withPlainBody(body: String) =
    Request(req.
      withBody(PlainTextBody(body)).
      withHeader("Content-Type", "text/plain; charset=UTF-8"))

  def noAuth: Request =
    Request(req, false)

  def send: AsyncCallback[Reply[String]] =
    performRequest.map({ case (status, body) => Reply(status, body) })
}


object Conversions {
  implicit class OfReply(reply: AsyncCallback[Reply[String]]) {
    def forStatus(code: Int): AsyncCallback[Reply[String]] =
      reply.flatMap({ reply =>
        if (reply.statusCode == code)
          AsyncCallback.pure(reply)
        else {
          Callback.log("unexpected status code: " + reply.statusCode + " instead of " + code + ".").async >>
          AsyncCallback.throwException(new Error(
            "unexpected status code: " + reply.statusCode))
        }
      })

    def forJson[A](implicit ev: Serialisable[A]): AsyncCallback[Reply[A]] =
      reply.map(reply => reply.copy(body = read[A](reply.body)))
  }

  implicit class OfAnyReply[A](reply: AsyncCallback[Reply[A]]) {
    def body: AsyncCallback[A] =
      reply.map({ reply => reply.body })

    // def withError[A](display: Throwable => A): AsyncCallback[Reply[A]] =
    //   reply.attemptTry.map({
    //     case Success(a) => a
    //     case Failure(e) => display(e)
    //   })
  }
}


object Request {
  private val baseUrl: String = BaseUrl.fromWindowOrigin.value

  def apply(request: HttpRequest, authorized: Boolean = true) =
    new Request(request, authorized)


  def apply(target: ApiPath, params: String*) = {
    val method = target.method match {
      case "GET" => Method.GET
      case "POST" => Method.POST
    }
    createRequest(method, target.write(params : _*))
  }

  private def createRequest(method: Method, url: String): Request =
    Request(HttpRequest(baseUrl + url).
      withMethod(method).
      withHeader("Accept", "application/json"))

  def get(url: String) = createRequest(Method.GET, url)
  def post(url: String) = createRequest(Method.POST, url)

  def get(path: ApiPath, params: String*) =
    createRequest(Method.GET, path.write(params : _*))
  def post(path: ApiPath, params: String*) =
    createRequest(Method.POST, path.write(params : _*))
  def delete(path: ApiPath, params: String*) =
    createRequest(Method.DELETE, path.write(params : _*))
}
