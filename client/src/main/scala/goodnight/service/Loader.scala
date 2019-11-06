
package goodnight.service

import japgolly.scalajs.react._
import scala.util.{ Try, Success, Failure }

import goodnight.model


object Loader {


  def loadScene(urlname: String): AsyncCallback[Try[model.Scene]] =
    AsyncCallback.pure(Failure(new java.lang.Error(
      "too lazy to implement this."))).
      delayMs(2000)

  def loadStory(urlname: String): AsyncCallback[Try[model.Story]] =
    AsyncCallback.pure(Failure(new java.lang.Error(
      "too lazy to implement this."))).
      delayMs(2000)

}
