
package goodnight.service

import japgolly.scalajs.react._
import org.scalajs.dom.ext.LocalStorage
import org.scalajs.dom.window
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.collection.mutable.Buffer
import scala.util.{Try, Failure, Success}

import goodnight.common.ApiV1
import goodnight.common.api.User._
import goodnight.model.User
import goodnight.service.Conversions._


trait AuthenticationService {
  def getUser: Option[User]
  def onUserChange(handler: Option[User] => Unit): Unit
  def signOut: Unit
}


object AuthenticationService
    extends AuthenticationService {
  private val userKey = "auth-user"
  private var registered = false
  private var changeListener: Buffer[Option[User] => Unit] = Buffer.empty

  private def register: Unit = {
    TokenStore.onChange(updateUser(_))
    updateUser(TokenStore.get)
    registered = true
  }

  def getUser: Option[User] = {
    LocalStorage("auth-user").
      flatMap(json => Json.fromJson(Json.parse(json)).asOpt)
  }

  def onUserChange(handler: Option[User] => Unit): Unit = {
    register
    changeListener += handler
  }

  private def updateUser(token: Option[String]): Unit = token match {
    case Some(token) =>
      println("requesting current user...")
      Request.get(ApiV1.Self).send.forJson.map({
        case Reply(200, Success(userJson)) =>
          println(s"got new user: $userJson")
          val user = userJson.as[User]
          LocalStorage.update(userKey, Json.stringify(userJson))
          changeListener.foreach(listener => listener(Some(user)))
        case err =>
          println(s"an error occurred during user profile fetch: $err")
          TokenStore.clear
          LocalStorage.remove(userKey)
      }).toCallback.runNow
    case None =>
      LocalStorage.remove(userKey)
      changeListener.foreach(listener => listener(None))
  }

  def signOut: Unit = {
    Request.delete(ApiV1.SignOut).send.toCallback.runNow
  }
}
