
package goodnight.server

import scala.concurrent.ExecutionContext
import play.api.mvc.Result
import controllers.Assets.Conflict

import goodnight.server.PostgresProfile.api._



case class EmptyOrConflict[T](query: DBIO[Option[T]]) {
  def flatMap(cont: Unit => DBIO[Result])(
    implicit ec: ExecutionContext): DBIO[Result] =
    query.flatMap({
      case None => cont(())
      case Some(_) => DBIO.successful(
        Conflict("The new element already exists."))
    })
}
