
package goodnight.api.authentication

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.impl.authenticators.BearerTokenAuthenticator
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}
import slick.jdbc.PostgresProfile.api._

import goodnight.db
import goodnight.model
import goodnight.server.PostgresProfile.Database


// todo: improve performance by a weak-referenced, size-limited cache map.
// todo: remove deprecated entries from database, if silhouette does not
// make sure of that. (i.e. expiration < now)
class DatabaseAuthenticatorRepository(
  database: Database)(
  implicit ec: ExecutionContext)
    extends AuthenticatorRepository[BearerTokenAuthenticator] {

  private def btaOfBt(bt: model.BearerToken) =
    BearerTokenAuthenticator(bt.id,
      LoginInfo(bt.provider, bt.key),
      new DateTime(bt.lastUsed),
      new DateTime(bt.expiration),
      bt.timeout.map(t => FiniteDuration(t, MILLISECONDS)))

  private def btOfBta(bta: BearerTokenAuthenticator) =
    model.BearerToken(bta.id,
      bta.loginInfo.providerID,
      bta.loginInfo.providerKey,
      bta.lastUsedDateTime.getMillis,
      bta.expirationDateTime.getMillis,
      bta.idleTimeout.map(_.toMillis))

  def add(bta: BearerTokenAuthenticator): Future[BearerTokenAuthenticator] = {
    // println(s"storing authenticator ${bta.id} -> $bta")
    database.run(db.BearerToken().insert(btOfBta(bta))).map(_ => bta)
  }

  def find(id: String): Future[Option[BearerTokenAuthenticator]] = {
    // println(s"finding authenticator $id")
    val query = db.BearerToken().
      filter(_.id === id).
      take(1).result.headOption
    database.run(query).map(_.map(btaOfBt))
  }

  def remove(id: String): Future[Unit] = {
    // println(s"removing authenticator $id")
    val query = db.BearerToken().
      filter(_.id === id).
      delete
    database.run(query).map(_ => ())
  }

  def update(bta: BearerTokenAuthenticator):
      Future[BearerTokenAuthenticator] = {
    // println(s"updating authenticator ${bta.id} -> $bta")
    val query = db.BearerToken().
      filter(_.id === bta.id).
      take(1).
      update(btOfBta(bta))
    database.run(query).map(_ => bta)
  }
}
