
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


  // def defaultAction(story: model.Story, player: model.Player) =
  //   model.PlayerAction(
  //     UUID.randomUUID(),
  //     story.id,
  //     player.id,
  //     0,
  //     model.Action.Location,
  //     None)//story.startLocation))

  // def getStory(urlname: String) =
  //   auth.SecuredAction.async(request =>
  //     database.run(
  //       GetOrNotFound(db.Story.ofUrlname(urlname)).flatMap(story =>
  //         db.Player.ofStory(request.identity.user.id, story.id).
  //           map(player => Ok((story, player))))))
  //             // ,
  //             // player.map(defaultAction(story, _))))))))
}
