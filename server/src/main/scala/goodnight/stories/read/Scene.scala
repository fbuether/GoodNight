
package goodnight.stories.read

import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import play.api.mvc.Result

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.logic.SceneParser
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Scenes(components: ControllerComponents,
  database: Database,
  player: Player,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  def getScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(request =>
      database.run(
        GetOrNotFound(db.Scene.named(storyUrlname, sceneUrlname)).map(scene =>
          Ok(scene.model))))


  def doScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
          GetOrNotFound(player.loadPlayer(request.identity.user.name,
            storyUrlname)).flatMap(playerState =>
            GetOrNotFound(db.Scene.named(storyUrlname, sceneUrlname)).
              flatMap(scene =>
                SceneView.ofScene(story, scene).flatMap(sceneView =>
                  Activity.doScene(playerState._1, playerState._2, scene).
                    map(activityState =>
                      Accepted((activityState._1.model, sceneView)))))))))
}
