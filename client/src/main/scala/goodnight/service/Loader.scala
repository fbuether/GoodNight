
package goodnight.service

import japgolly.scalajs.react._
import scala.util.{ Try, Success, Failure }

import goodnight.model
import goodnight.common.ApiV1
import goodnight.common.api.Story._
import goodnight.service.Conversions._


object Loader {


  def loadScene(urlname: String): AsyncCallback[Try[model.Scene]] =
    AsyncCallback.pure(Failure(new java.lang.Error(
      "too lazy to implement this."))).
      delayMs(2000)


  def loadStory(urlname: String): AsyncCallback[Try[model.Story]] =
    Request(ApiV1.Story, urlname).send.forJson.map({
      case Reply(200, Success(storyJson)) =>
        Success(storyJson.as[model.Story])
      case Reply(code, content) =>
        Failure(new Error(s"Invalid server reply (code $code): $content"))
    })
}
