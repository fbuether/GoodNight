
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

  // parse a json body sent with the request to a specified shape.
  // use like:
  // = Action.async(parse.json)(
  //  withJsonAs((request: Request[JsValue], data: Data) => {
  // ...
  def withJsonAs[A,R](innerAction: ((Request[JsValue], A) => Future[Result]))(
    request: Request[JsValue])(implicit reads: Reads[A]): Future[Result] = {
    request.body.validate[A] match {
      case JsSuccess(data, _) => innerAction(request, data)
      case JsError(errors) =>
        val errorMessage = ujson.Obj(
          "success" -> false,
          "errors" -> errors.map({ case (p,ves) =>
            ujson.Obj(
              "path" -> p.toString,
              "errors" -> ves.map({ case JsonValidationError(a) =>
                a}))}))
        Future.successful(
          BadRequest(write(errorMessage)))
    }
  }

  def parseJson[A](body: JsValue, innerAction: A => Future[Result])(
    implicit reads: Reads[A]): Future[Result] = {
    body.validate[A] match {
      case JsSuccess(data, _) => innerAction(data)
      case JsError(errors) =>
        val errorMessage = ujson.Obj(
          "success" -> false,
          "errors" -> errors.map({ case (p,ves) =>
            ujson.Obj(
              "path" -> p.toString,
              "errors" -> ves.map({ case JsonValidationError(a) =>
                a}))}))
        Future.successful(
          BadRequest(write(errorMessage)))
    }
  }



  def parseAsJson[A](implicit ev: Serialisable[A]): BodyParser[A] =
    parse.when(_.contentType.exists(_.equalsIgnoreCase("application/json")),
      parse.raw.validate({ buffer =>
        try Right(read[A](buffer.asBytes().map(_.utf8String).getOrElse("")))
        catch { case (e: Throwable) =>
          val errorMessage = ujson.Obj(
            "success" -> false,
            "error" -> "json parse error",
            "detail" -> ujson.Arr(
              e.toString,
              ujson.Str(Option(e.getCause).map(_.toString).
                getOrElse("no inner cause")),
              e.getMessage))
          Left(BadRequest(write(errorMessage)).as(JSON))
        }
      }), { request =>
        Future.successful(
          UnsupportedMediaType(write(ujson.Obj(
            "success" -> false,
            "error" -> ("Invalid content type. "+
              "Expected application/json body.")))).as(JSON)) })
}

