
package goodnight.api

import com.mohiva.play.silhouette.api.Silhouette

import goodnight.api.authentication.AuthEnvironment


package object authentication {
  type AuthService = Silhouette[AuthEnvironment]
}
