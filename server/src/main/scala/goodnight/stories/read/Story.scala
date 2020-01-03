
package goodnight.stories.read

import java.util.UUID
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
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
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  private def getStoryQuery(identity: Option[Id], myself: Boolean,
    publicOnly: Boolean) =
    (identity, myself, publicOnly) match {
      case (Some(user), true, _) => db.Story.ofUser(user.user.name)
      case (_, _, true) => db.Story.allPublic
      case _ => db.Story.all
    }

  def getAvailableStories(query: Map[String, Seq[String]]) =
    auth.UserAwareAction.async(request =>
      database.run(
        getStoryQuery(request.identity, query.contains("myself"),
          // send only public stories if requested or not logged in
          query.contains("publicOnly") || !request.identity.isDefined).
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

  private def toView(story: db.model.Story, pa: Option[(_, db.model.Scene)]):
      DBIO[Option[model.SceneView]] =
    pa match {
      case Some((_, scene)) =>
        SceneView.ofScene(story, scene).map(Some.apply)
      case None => DBIO.successful(None)
    }



  type PlayerState = (model.Player, model.Activity, model.SceneView)
  def loadPlayerState: DBIO[Option[PlayerState]] =
    playerController.loadPlayer(identity.user.name, story.urlname).
      flatMap(playerStateOpt =>
        loadPlayerActivity(playerStateOpt).flatMap(playerActivityOpt =>
          toView(story, playerActivityOpt).map(sceneViewOpt =>

            playerStateOpt.flatMap(ps =>
              playerActivityOpt.flatMap(pa =>
                sceneViewOpt.map(sv =>
                  (ps._1.model(ps._2), pa._1.model, sv)))))))


  def withOptionalPlayerState(story: db.model.Story,
    identity: Option[Id], cont: Option[PlayerState] => Result):
      DBIO[Result] = identity match {
    case None =>
      // todo: denote in the reply that this is for a non-logged-in-user,
      // so the front end can request a temporary user.
      DBIO.successful(cont(None))
    case Some(identity) => loadPlayerState().map(cont)
  }

  def getStory(urlname: String) =
    auth.UserAwareAction.async(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(urlname)).flatMap(story =>
          withOptionalPlayerState(story, request.identity, state =>
            Ok(story.model, state)))))
}
