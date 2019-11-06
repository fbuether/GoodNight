
package goodnight.common.api

import java.util.UUID

import play.api.libs.json._
import play.api.libs.functional.syntax._

import goodnight.model.{ Scene => MScene }

// todo: http://www.lihaoyi.com/upickle/

object Scene {
  implicit val sceneReads: Reads[MScene] =
    ((JsPath \ "id").read[UUID] and
      (JsPath \ "story").read[UUID] and
      (JsPath \ "raw").read[String] and
      (JsPath \ "title").read[String] and
      (JsPath \ "urlname").read[String] and
      (JsPath \ "image").read[String] and
      (JsPath \ "location").readNullable[UUID] and
      (JsPath \ "text").read[String] and
      (JsPath \ "mandatory").read[Boolean])(
      MScene.apply _)

  implicit val sceneWrites: Writes[MScene] =
    ((JsPath \ "id").write[UUID] and
      (JsPath \ "story").write[UUID] and
      (JsPath \ "raw").write[String] and
      (JsPath \ "title").write[String] and
      (JsPath \ "urlname").write[String] and
      (JsPath \ "image").write[String] and
      (JsPath \ "location").writeNullable[UUID] and
      (JsPath \ "text").write[String] and
      (JsPath \ "mandatory").write[Boolean])(
      unlift(MScene.unapply))
}
