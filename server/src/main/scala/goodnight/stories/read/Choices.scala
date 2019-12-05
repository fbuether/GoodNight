
package goodnight.stories.read

import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import play.api.mvc.Result

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Choices(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def doChoice(storyUrlname: String, sceneUrlname: String,
    choiceUrlname: String) =
    auth.SecuredAction.async(request =>
    // todo: save the current player state to be at this scene.
      database.run(
        GetOrNotFound(db.Choice.ofUrlname(storyUrlname, sceneUrlname,
          choiceUrlname)).map(Ok(_))))
}
