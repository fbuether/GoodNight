package goodnight.server

import akka.util.ByteString
import play.api.http.Writeable
import play.api.mvc.BaseController
import play.api.mvc.BodyParser
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import goodnight.server.PostgresProfile.api._


case class GetOr[T](error: Result)(query: DBIO[Option[T]]) {
  def flatMap(cont: T => DBIO[Result])(
    implicit ec: ExecutionContext): DBIO[Result] =
    query.flatMap({
      case Some(element) => cont(element)
      case None => DBIO.successful(error)
    })

  def map(cont: T => Result)(
    implicit ec: ExecutionContext): DBIO[Result] =
    flatMap(t => DBIO.successful(cont(t)))
}
