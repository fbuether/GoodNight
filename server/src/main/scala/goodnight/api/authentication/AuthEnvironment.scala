
package goodnight.api.authentication

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.BearerTokenAuthenticator
import com.mohiva.play.silhouette.api.Identity

import goodnight.model.User


case class Id(user: User) extends Identity


trait AuthEnvironment extends Env {
  type I = Id
  type A = BearerTokenAuthenticator
}
