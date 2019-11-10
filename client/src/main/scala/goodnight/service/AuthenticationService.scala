
package goodnight.service

import japgolly.scalajs.react._
import org.scalajs.dom.ext.LocalStorage
import scala.collection.mutable.Buffer
import scala.util.{Try, Success, Failure}

import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.model.User
import goodnight.service.Conversions._


object AuthenticationService {
  private val userKey = "auth-user"
  private var changeListener: Buffer[Option[User] => Unit] = Buffer.empty

  def getUser: Option[User] = {
    LocalStorage(userKey).
      flatMap(json => Json.fromJson(Json.parse(json)).asOpt)
  }

  def isSignedIn: CallbackTo[Boolean] =
    CallbackTo(getUser.isDefined)

  def onUserChange(handler: Option[User] => Unit): Unit = {
    changeListener += handler
    handler(getUser)
  }

  def loginWith(identity: String, password: String):
      AsyncCallback[User] = {
    Request(ApiV1.Authenticate).withBody(Json.obj(
      "identity" -> identity,
      "password" -> password)).
      noAuth.
      send.forJson.
      flatMap({
        case Reply(202, Success(userJson)) =>
          val user = userJson.as[User]
          LocalStorage.update(userKey, Json.stringify(userJson))
          changeListener.foreach(_(Some(user)))
          AsyncCallback.pure(user)
        case Reply(401, b) =>
          signOut.asAsyncCallback >>
          AsyncCallback.throwException(new Error(
            "Could not authenticate with the given email and password."))
        case Reply(c, b) =>
          println(s"an error occurred during user profile fetch: $c / $b")
          signOut.asAsyncCallback >>
          AsyncCallback.throwException(new Error("Error: " + c + " ~ " + b))
      })
  }

  def signOut: Callback = {
    Callback({
      TokenStore.clear
      LocalStorage.remove(userKey)
      changeListener.foreach(_(None))
    }) >>
    getUser.map(_ => Request(ApiV1.SignOut).send.toCallback).
      getOrElse(Callback.empty)
  }
}
