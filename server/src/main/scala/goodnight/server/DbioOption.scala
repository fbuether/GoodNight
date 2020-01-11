
package goodnight.server

import scala.concurrent.ExecutionContext

import goodnight.server.PostgresProfile.api._


case class DbOption[A](base: DBIO[Option[A]])(
  implicit ec: ExecutionContext) {
  def flatMap[B](cont: A => DBIO[Option[B]]): DBIO[Option[B]] =
    base.flatMap({
      case None => DBIO.successful(None)
      case Some(value) => cont(value)
    })

  def map[B](cont: A => B): DBIO[Option[B]] =
    flatMap(value => DBIO.successful(Some(cont(value))))
}
