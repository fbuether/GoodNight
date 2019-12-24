
package goodnight.stories.read

import java.util.UUID
import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._

import goodnight.api.authentication.AuthService
import goodnight.api.authentication.Id
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Stories(components: ControllerComponents,
  database: Database,
  playerController: Player,
  sceneController: Scenes,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  private def getStoryQuery(identity: Option[Id], myself: Boolean) =
    (identity, myself) match {
      case (Some(user), true) => db.Story.ofUser(user.user.name)
      case _ => db.Story.allPublic
    }

  def getAvailableStories(query: Map[String, Seq[String]]) =
    auth.UserAwareAction.async(request =>
      database.run(
        getStoryQuery(request.identity, query.contains("myself")).
          map(stories => Ok(stories.map(_.model)))))


  def defaultActivity(story: db.model.Story, player: db.model.Player,
    defScene: String) =
    model.Activity(
      story.name,
      player.name,
      0,
      defScene,
      Seq(),
      Map())

  private def loadPlayerActivity(playerState: Option[(db.model.Player, _)]):
      DBIO[Option[(db.model.Activity, db.model.Scene)]] =
    playerState match {
      case Some(state) => playerController.loadActivity(state._1)
      case None => DBIO.successful(None)
    }

  private def toView(pa: Option[(_, db.model.Scene)]):
      DBIO[Option[model.SceneView]] =
    pa match {
      case Some((_, scene)) => sceneController.toView(scene).map(Some.apply)
      case None => DBIO.successful(None)
    }

  def getStory(urlname: String) =
    auth.SecuredAction.async(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(urlname)).flatMap(story =>
          playerController.loadPlayer(request.identity.user.name,
            story.urlname).flatMap(playerStateOpt =>
            loadPlayerActivity(playerStateOpt).flatMap(playerActivityOpt =>
              toView(playerActivityOpt).map(sceneViewOpt =>
                Ok(story.model, playerStateOpt.flatMap(ps =>
                  playerActivityOpt.flatMap(pa =>
                    sceneViewOpt.map(sv =>
                      (ps._1.model(ps._2), pa._1.model, sv)))))))))))
}
