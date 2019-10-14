
package goodnight.common.api

import java.util.UUID

import play.api.libs.json._
import play.api.libs.functional.syntax._

import goodnight.model.{ Story => MStory }

// todo: http://www.lihaoyi.com/upickle/

object Story {
  implicit val storyReads: Reads[MStory] =
    ((JsPath \ "id").read[UUID] and
      (JsPath \ "creator").read[UUID] and
      (JsPath \ "name").read[String] and
      (JsPath \ "urlname").read[String] and
      (JsPath \ "image").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "startLocation").readNullable[UUID])(
      MStory.apply _)

  implicit val storyWrites: Writes[MStory] =
    ((JsPath \ "id").write[UUID] and
      (JsPath \ "creator").write[UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "urlname").write[String] and
      (JsPath \ "image").write[String] and
      (JsPath \ "description").write[String] and
      (JsPath \ "startLocation").writeNullable[UUID])(
      unlift(MStory.unapply))
}
