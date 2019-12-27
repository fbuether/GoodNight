
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
  sceneController: Scenes,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  type PlayerState = (db.model.Player, Seq[db.model.State])
  type PlayerActivity = (db.model.Activity, db.model.Scene)

  private def withState(playerOpt: Option[db.model.Player]):
      DBIO[Option[PlayerState]] =
    playerOpt match {
      case Some(player) => db.State.ofPlayer(player.user, player.story).
          map(state => Some(player, state))
      case None => DBIO.successful(None)
    }

  def loadPlayer(user: String, story: String): DBIO[Option[PlayerState]] =
    db.Player.ofStory(user, story).flatMap(withState)



  def loadActivity(player: db.model.Player): DBIO[Option[PlayerActivity]] =
    db.Activity.newest(player.story, player.user).flatMap({
      case None => DBIO.successful(None)
      case Some(activity) =>
        db.Scene.named(player.story, activity.scene).map(sceneOpt =>
          sceneOpt.map(scene => (activity, scene)))
    })


  case class WithName(name: String)
  implicit val serialise_WithName: Serialisable[WithName] = macroRW

  def createPlayer(storyUrlname: String) =
    auth.SecuredAction.async(parseFromJson[WithName])(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
          // todo: verify that this user does not yet have a player for
          // this story.
          GetOrNotFound(db.Scene.defaultOfStory(story.urlname)).flatMap(scene =>
            Activity.createNewPlayer(request.identity.user,
              story, request.body.name).flatMap(pi =>
              sceneController.toView(story, pi._4).map(sceneView =>
                // todo: fetch player state as given by first scene.
                Created(
                  (pi._1.model(pi._2),
                    pi._3.model,
                    sceneView))))))))
}
