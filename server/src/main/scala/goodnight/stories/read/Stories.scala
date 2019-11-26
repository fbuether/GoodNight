
package goodnight.stories.read

import java.util.UUID
import play.api.db.slick.DbName
import play.api.libs.functional.syntax._
import play.api.libs.json.{ JsValue, Json, Reads, JsPath, JsArray }
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
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


  def getAvailableStories(query: Map[String, Seq[String]]) =
    auth.UserAwareAction.async({ request =>
      val select = (request.identity, query.get("myself")) match {
        case (Some(user), Some(_)) => db.Story.ofUser(user.user.id)
        case _ => db.Story.allPublic
      }
      database.run(select).map(Ok(_))
    })
}
