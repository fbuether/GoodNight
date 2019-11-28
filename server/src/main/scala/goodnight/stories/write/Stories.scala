
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


class Stories(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  private def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase


  case class WithName(name: String)
  implicit val serialise_WithName: Serialisable[WithName] = macroRW

  def createStory =
    auth.SecuredAction.async(parseFromJson[WithName])({ request =>
      def existsError(s: model.Story) = DBIO.successful(Conflict(error(
        "A story with this name already exists.")))
      def insertStory = db.Story.insert(model.Story(UUID.randomUUID(),
        request.identity.user.id,
        request.body.name,
        urlnameOf(request.body.name),
        "Moon.png",
        "",
        None)).
        map(Ok(_))

      database.run(
        db.Story.ofUrlname(urlnameOf(request.body.name)).
          flatMap(_.fold(insertStory)(existsError)))
    })
}
