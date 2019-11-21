
package goodnight.service

import japgolly.scalajs.react._
import org.scalajs.dom.ext.LocalStorage
import scala.collection.mutable.Buffer
import scala.util.{Try, Success, Failure}

import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.model
import goodnight.service.Conversions._


object AuthenticationService {
  private val userKey = "auth-user"
  private var changeListener: Buffer[Option[model.User] => Unit] = Buffer.empty
  private var startVerify = false
  private var verified = false // todo: have we verified the auth this session?

  def verifyIfRequired = {
    if (!verified && !startVerify) {
      startVerify = true
      getUser match {
        case None =>
          verified = true
        case Some(storedUser) =>
          println("verifying user.")
          startVerify = true
          Request(ApiV1.Self).send.forJson[model.User].map({
            a => verified = true; a
          }).completeWith({
            case Success(Reply(200, user)) => Callback({
              if (storedUser != user) {
                LocalStorage.update(userKey, write(user))
                changeListener.foreach(_(Some(user)))
              }
            })
            case r =>
              signOut
          }).runNow
      }
    }
  }

  def getUser: Option[model.User] = {
    LocalStorage(userKey).
      flatMap(readMaybe[model.User])
  }

  def isSignedIn: CallbackTo[Boolean] =
    CallbackTo(getUser.isDefined)

  def onUserChange(handler: Option[model.User] => Unit): Unit = {
    changeListener += handler
    verifyIfRequired
    handler(getUser)
  }

  def loginWith(identity: String, password: String):
      AsyncCallback[model.User] = {
    Request(ApiV1.Authenticate).withBody(ujson.Obj(
      "identity" -> identity,
      "password" -> password)).
      noAuth.
      send.
      forJson[model.User].
      flatMap({
        case Reply(202, user) =>
          LocalStorage.update(userKey, write(user))
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
