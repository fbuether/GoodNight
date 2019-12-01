
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


class Scenes(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def getAvailableScenes(storyUrlname: String, location: Option[String]) =
    auth.SecuredAction.async(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap({story =>
          val userId = request.identity.user.id
          GetOrNotFound(db.Player.ofStory(userId, story.id)).flatMap(player =>
            db.Scene.forPlayer(story.id, player.location).map(Ok(_)))
        })))

  def getAllScenes(storyUrlname: String) =
    auth.SecuredAction.async(request =>
      database.run(
        db.Scene.allOfStory(storyUrlname).map(Ok(_))))

  def doScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(request =>
    // todo: save the current player state to be at this scene.
      database.run(
        GetOrNotFound(db.Scene.ofStory(storyUrlname, sceneUrlname)).
          flatMap(scene =>
            db.Choice.ofScene(scene.id).map(choices =>
              Ok((scene, choices))))))
}
