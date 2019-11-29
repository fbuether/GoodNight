
package goodnight.stories.write

import java.util.UUID
import play.api.db.slick.DbName
import play.api.libs.functional.syntax._
import play.api.libs.json.{ JsValue, Json, Reads, JsPath, JsArray }
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.api.authentication.AuthService
import goodnight.api.authentication.Id
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database
import goodnight.logic.SceneParser


class Scenes(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  case class WithText(text: String)
  implicit val serialise_WithText: Serialisable[WithText] = macroRW

  def createScene(storyUrlname: String) =
    auth.SecuredAction.async(parseFromJson[WithText])(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
          SceneParser.parseScene(story, request.body.text).fold(
            err => DBIO.successful(UnprocessableEntity(error(err))),
            parsed =>
            EmptyOrConflict(db.Scene.ofStory(storyUrlname, parsed._1.urlname)).
              andThen(
                db.Scene.insert(parsed._1).flatMap(insertedScene =>
                  DBIO.sequence(parsed._2.map(db.Choice.insert)).
                    map(_ => Created(insertedScene))))))))

}
