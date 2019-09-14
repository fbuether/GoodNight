
package goodnight.service

import org.scalajs.dom.window

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Broadcaster

case class User(name: String)


object AuthenticationService {
  private val authStore = "authentication"

  def setAuthentication(token: String): Callback =
    Callback(window.localStorage.setItem(authStore, token)) >>
      LoginEvents.broadcast(Some(User("username")))


  def removeAuthentication: Callback =
    Callback(window.localStorage.removeItem(authStore)) >>
      LoginEvents.broadcast(None)

  def getAuthentication: CallbackTo[Option[String]] = CallbackTo {
    window.localStorage.getItem(authStore) match {
      case null => None
      case token => Some(token)
    }
  }

  object LoginEvents extends Broadcaster[Option[User]] {
    override def broadcast(a: Option[User]): Callback =
      super.broadcast(a)
  }

  def isLoggedIn: CallbackTo[Boolean] = getAuthentication.
    // map(_.isValid).
    map(_.nonEmpty)

  def getUser: CallbackTo[Option[User]] = getAuthentication.
    map(_.map(_ => User("username")))
}
