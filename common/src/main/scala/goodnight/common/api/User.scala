
package goodnight.common.api

import java.util.UUID

import play.api.libs.json._
import play.api.libs.functional.syntax._

import goodnight.model.{ User => MUser }


object User {
  implicit val userReads: Reads[MUser] =
    ((JsPath \ "id").read[UUID] and
      (JsPath \ "name").read[String])(
      MUser.apply _)

  implicit val userWrites: Writes[MUser] =
    ((JsPath \ "id").write[UUID] and
      (JsPath \ "name").write[String])(
      unlift(MUser.unapply))
}
