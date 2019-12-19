
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
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  private def getStoryQuery(identity: Option[Id], myself: Boolean) =
    (identity, myself) match {
      case (Some(user), true) => db.Story.ofUser(user.user.name)
      case _ => db.Story.allPublic
    }

  def getAvailableStories(query: Map[String, Seq[String]]) =
    auth.UserAwareAction.async( request =>
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

  def getStory(urlname: String) =
    auth.SecuredAction.async(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(urlname)).flatMap(story =>
          playerController.loadPlayer(request.identity.user.name,
            story.urlname).map(playerStateOpt =>
            Ok(story.model, playerStateOpt.map(ps =>
              // todo: insert activity and scene of current player.
              (ps._1.model(ps._2), Unit, Unit)))))))
}
