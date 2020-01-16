
package goodnight.server

import akka.util.ByteString
import play.api.http.Writeable
import play.api.mvc.BaseController
import play.api.mvc.BodyParser
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

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


  // make the return type of this result explicit
  protected def result[A](status: Status, body: A)(
    implicit ev: Serialisable[A]): Result =
    status(body)


  // for writing json values, or any json-able values, directly:
  implicit def jsonWriteableA[A](implicit ev: Serialisable[A]): Writeable[A] =
    Writeable(data => ByteString(write(data)), Some("application/json"))

  implicit val jsonWriteable: Writeable[ujson.Value] =
    Writeable(data => ByteString(ujson.write(data)), Some("application/json"))


  def error(message: String): ujson.Obj =
    ujson.Obj(
      "success" -> false,
      "error" -> ujson.Str(message))

  def notFound(message: String): Future[Result] =
    Future.successful(NotFound(error(message)))


  // extract option results from database queries
  protected case class GetOrNotFound[T](query: DBIO[Option[T]]) {
    def flatMap(cont: T => DBIO[Result]): DBIO[Result] =
      query.flatMap({
        case Some(element) => cont(element)
        case None => DBIO.successful(NotFound(error(
          "The requested element does not exist.")))
      })

    def map(cont: T => Result): DBIO[Result] =
      flatMap(t => DBIO.successful(cont(t)))
  }

  protected case class EmptyOrConflict[T](query: DBIO[Option[T]]) {
    def andThen(cont: => DBIO[Result]): DBIO[Result] =
      query.flatMap({
        case None => cont
        case Some(_) => DBIO.successful(Conflict(error(
        "The element to save already exists.")))
      })
  }
}

