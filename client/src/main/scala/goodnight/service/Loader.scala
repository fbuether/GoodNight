
package goodnight.service

import japgolly.scalajs.react._
import scala.util.{ Try, Success, Failure }

import goodnight.model
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.service.Conversions._


object Loader {
  def loadScene(storyUrlname: String, urlname: String):
      AsyncCallback[model.Scene] =
    Request(ApiV1.Scene, storyUrlname, urlname).send.
      forStatus(200).
      forJson[model.Scene].
      body

  def loadStory(urlname: String): AsyncCallback[model.Story] =
    Request(ApiV1.Story, urlname).send.
      forStatus(200).
      forJson[model.Story].
      body

  def loadScenes(storyUrlname: String): AsyncCallback[List[model.Scene]] =
    Request(ApiV1.Scenes, storyUrlname).send.
      forStatus(200).
      forJson[List[model.Scene]].
      body
}
