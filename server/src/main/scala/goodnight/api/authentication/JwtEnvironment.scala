
package goodnight.api.authentication

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

import goodnight.model.User


trait JwtEnvironment extends Env {
  type I = User
  type A = JWTAuthenticator
}
