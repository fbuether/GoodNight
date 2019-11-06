
package goodnight.service

import japgolly.scalajs.react._
import scala.util.{ Try, Success, Failure }

import goodnight.model
import goodnight.common.ApiV1
import goodnight.common.api.Story._
import goodnight.common.api.Scene._
import goodnight.service.Conversions._


object Loader {


  def loadScene(storyUrlname: String, urlname: String):
      AsyncCallback[Try[model.Scene]] =
    Request(ApiV1.Scene, storyUrlname, urlname).send.forJson.map({
      case Reply(200, Success(sceneJson)) =>
        sceneJson.as[model.Scene]
    }).attemptTry


  def loadStory(urlname: String): AsyncCallback[Try[model.Story]] =
    Request(ApiV1.Story, urlname).send.forJson.map({
      case Reply(200, Success(storyJson)) =>
        storyJson.as[model.Story]
    }).attemptTry
}
