
package goodnight.service

import org.scalajs.dom.window

import japgolly.scalajs.react._


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
}
