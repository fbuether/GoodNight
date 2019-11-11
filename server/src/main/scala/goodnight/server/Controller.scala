
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
  components: ControllerComponents)
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

  class JsonBodyParsers(
    config: ParserConfiguration,
    errorHandler: HttpErrorHandler,
    materializer: Materializer,
    temporaryFileCreator: TemporaryFileCreator)
      extends PlayBodyParsers {

val config = new play.api.http.ParserConfiguration()

    // largely inspired by Play's default text body parser.
    def fromJson[A](implicit ev: Serialisable[A]): BodyParser[A] =
      when(_.contentType.exists(_.equalsIgnoreCase("application/json")),
        tolerantBodyParser[A]("goodnight json upickled",
          DefaultMaxTextLength, // by default 512kb
          "Error decoding json body")({ (request, bytes) =>
            read[A](bytes.utf8String)
          }),
        createBadResult("Expected application/json body",
          UNSUPPORTED_MEDIA_TYPE))


    def asJson: BodyParser[ujson.Value] =
      when(_.contentType.exists(_.equalsIgnoreCase("application/json")),
        tolerantBodyParser[ujson.Value]("goodnight json ujsoned",
          DefaultMaxTextLength, // by default 512kb
          "Error decoding json body")({ (request, bytes) =>
            ujson.read(bytes.utf8String)
          }),
        createBadResult("Expected application/json body",
          UNSUPPORTED_MEDIA_TYPE))
  }

  def parse = new JsonBodyComponentsrs(components.config,
    components.errorHandler,
    components.materializer,
    components.temporaryFileCreator)
}

