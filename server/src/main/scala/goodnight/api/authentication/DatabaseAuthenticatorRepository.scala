
package goodnight.api.authentication

import scala.collection.mutable.{ Map => MMap }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.impl.authenticators.BearerTokenAuthenticator

import goodnight.server.PostgresProfile.Database

// todo: extend this class to have actual database connectivity.
class DatabaseAuthenticatorRepository(
  db: Database)(
  implicit ec: ExecutionContext)
    extends AuthenticatorRepository[BearerTokenAuthenticator] {

  private type A = BearerTokenAuthenticator

  private var m = MMap[String, A]()

  def add(a: A): Future[A] = {
    m.put(a.id, a)
    println(s"storing authenticator ${a.id} -> $a")
    Future.successful(a)
  }

  def find(id: String): Future[Option[A]] = {
    println(s"finding authenticator $id")
    Future.successful(m.get(id))
  }

  def remove(id: String): Future[Unit] = {
    println(s"removing authenticator $id")
    Future.successful(m.remove(id).map(_ => ()))
  }

  def update(a: A): Future[A] = {
    m.put(a.id, a)
    println(s"updating authenticator ${a.id} -> $a")
    Future.successful(a)
  }
}
