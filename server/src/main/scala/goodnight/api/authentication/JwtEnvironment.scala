
package goodnight.api.authentication

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.Identity

import goodnight.model.User


case class Id(user: User) extends Identity


trait JwtEnvironment extends Env {
  type I = Id
  type A = JWTAuthenticator
}
