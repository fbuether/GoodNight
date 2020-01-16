
package goodnight.stories.read

import java.util.UUID
import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import play.api.mvc.Result

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.parser.SceneParser
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Scene(components: ControllerComponents,
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
    // todo: check if the player is in fact capable of doing this scene.
    auth.SecuredAction.async(request =>
      database.run(for (
        story <- GetOrNotFound(db.Story.ofUrlname(storyUrlname));
        scene <- GetOrNotFound(db.Scene.named(storyUrlname, sceneUrlname));
        qualities <- db.Quality.allOfStory(story.urlname);
        states <- db.State.ofPlayer(request.identity.user.name, story.urlname);
        lastActivity <- db.Activity.newest(storyUrlname,
          request.identity.user.name);
        readScene <- DBIO.successful(SceneView.parse(scene));

        (activity, effects) <- Activity.go(request.identity.user.name,
          qualities, states, lastActivity, readScene);

        // todo: optimise: compute these out of states and effects
        newStates <- db.State.ofPlayer(request.identity.user.name,
          story.urlname);
        choices <- db.Scene.namedList(story.urlname,
          SceneView.getChoices(story, scene).toList))
      yield result[model.read.Outcome](Accepted,
        (Convert.read(qualities, activity, effects),
          Convert.read(qualities, newStates, scene, choices)))))
}
