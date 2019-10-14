
package goodnight.service

import org.scalajs.dom.window
import goodnight.service.Storage

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Broadcaster



case class User(name: String)


object AuthenticationService {
  private val authStore = "authentication"

  def setAuthentication(token: String): Callback = Callback {
    println(s"setting authentication token: $token")
    Storage.set("auth-token", token)
  }

    // Callback(window.localStorage.setItem(authStore, token)) >>
    //   LoginEvents.broadcast(Some(User("username")))


  def removeAuthentication: Callback = Callback {
    Storage.remove("auth-token")
  }
    // Callback(window.localStorage.removeItem(authStore)) >>
    //   LoginEvents.broadcast(None)

  def getAuthentication: CallbackTo[Option[String]] = CallbackTo {
    val auth = Storage.get[String]("auth-token")
    println(s"reading authentication token: $auth")
    auth
  }

    // println("auth is: " + window.localStorage.getItem(authStore))
    // window.localStorage.getItem(authStore) match {
    //   case null => None
    //   case token => Some(token)
  //   }
  // }

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
