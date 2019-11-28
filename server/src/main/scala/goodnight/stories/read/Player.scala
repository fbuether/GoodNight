
package goodnight.stories.read

import java.util.UUID
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Player(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  case class WithName(name: String)
  implicit val serialise_WithName: Serialisable[WithName] = macroRW

  def createPlayer(storyUrlname: String) =
    auth.SecuredAction.async(parseFromJson[WithName])(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
          db.Player.insert(model.Player(UUID.randomUUID(),
            request.identity.user.id,
            story.id,
            request.body.name,
            story.startLocation)).map(Created(_)))))
}
