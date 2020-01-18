
package goodnight.server

import akka.util.ByteString
import play.api.http.Writeable
import play.api.mvc.BaseController
import play.api.mvc.BodyParser
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import play.api.mvc.Results.Status
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import goodnight.server.PostgresProfile.api._


case class GetOrEither[L,R](errorCont: L => Result)(
  query: DBIO[Either[L,R]])(implicit writeable: Writeable[L]) {
  def flatMap(cont: R => DBIO[Result])(
    implicit ec: ExecutionContext):
      DBIO[Result] =
    query.flatMap({
      case Right(element) => cont(element)
      case Left(error) => DBIO.successful(errorCont(error))
    })

  def map(cont: R => Result)(
    implicit ec: ExecutionContext): DBIO[Result] =
    flatMap(t => DBIO.successful(cont(t)))
}
