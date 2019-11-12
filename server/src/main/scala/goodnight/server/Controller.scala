
package goodnight.server

import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsonValidationError
import play.api.libs.json.Reads
import play.api.mvc.BaseController
import play.api.mvc.BodyParser
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.mvc.PlayBodyParsers

import play.api.http.ParserConfiguration
import play.api.http.HttpErrorHandler
import akka.stream.Materializer
import play.api.libs.Files.TemporaryFileCreator



import goodnight.common.Serialise._


class Controller(
  components: ControllerComponents)(
  implicit ec: ExecutionContext)
    extends BaseController {
  def controllerComponents: ControllerComponents =
    components


  protected def withJsonContentType[A](inner: BodyParser[A]): BodyParser[A] =
    parse.when(_.contentType.exists(_.equalsIgnoreCase("application/json")),
      inner,
      { request =>
        Future.successful(
          UnsupportedMediaType(write(ujson.Obj(
            "success" -> false,
            "error" -> ("Invalid content type. "+
              "Expected application/json body.")))).as(JSON)) })

  protected def errorFromException(e: Throwable) = {
    val errorMessage = ujson.Obj(
      "success" -> false,
      "error" -> "json parse error",
      "detail" -> ujson.Arr(
        e.toString,
        ujson.Str(Option(e.getCause).map(_.toString).
          getOrElse("no inner cause")),
        e.getMessage))
    BadRequest(write(errorMessage)).as(JSON)
  }

  // parse a json body sent with the request to a specified shape.
  // use like:
  // = Action.async(parseAsJson[model.Story])({ request =>
  // ...
  def parseFromJson[A](implicit ev: Serialisable[A]): BodyParser[A] =
    withJsonContentType(
      parse.raw.validate({ buffer =>
        try Right(read[A](buffer.asBytes().map(_.utf8String).getOrElse("")))
        catch { case (e: Throwable) => Left(errorFromException(e)) }
      }))


  def parseToJson: BodyParser[ujson.Value] =
    withJsonContentType(
      parse.raw.validate({ buffer =>
        try {
          val buf: String = buffer.asBytes().map(_.utf8String).getOrElse("")
          Right(ujson.read(buf))
        }
        catch { case (e: Throwable) => Left(errorFromException(e)) }
      }))
}

