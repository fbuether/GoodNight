
package goodnight.service

import org.scalajs.dom.window

import japgolly.scalajs.react._


case class User(name: String)


object AuthenticationService {
  private val authStore = "authentication"

  def setAuthentication(token: String): Callback = Callback {
    println("we are setting auth!")
    window.localStorage.setItem(authStore, token)
  }

  def removeAuthentication: Callback = Callback {
    window.localStorage.removeItem(authStore)
  }

  def getAuthentication: CallbackTo[Option[String]] = CallbackTo {
    window.localStorage.getItem(authStore) match {
      case "" => None
      case token => Some(token)
    }
  }

  def isLoggedIn: CallbackTo[Boolean] = getAuthentication.
    // map(_.isValid).
    map(_.nonEmpty)

  def getUser: CallbackTo[Option[User]] = getAuthentication.
    map(_.map(_ => User("username")))
}
