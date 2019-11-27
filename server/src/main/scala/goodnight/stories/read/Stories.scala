
package goodnight.stories.read

import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._

import goodnight.api.authentication.AuthService
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

  def getAvailableStories(query: Map[String, Seq[String]]) =
    auth.UserAwareAction.async({ request =>
      val select = (request.identity, query.get("myself")) match {
        case (Some(user), Some(_)) => db.Story.ofUser(user.user.id)
        case _ => db.Story.allPublic
      }
      database.run(select).map(Ok(_))
    })

  def getStory(urlname: String) =
    auth.SecuredAction.async({ request =>
      val select = db.Story.ofUrlname(urlname).flatMap({
        case Some(story) =>
          db.Player.ofStory(request.identity.user.id, story.id).map(player =>
            Ok((story, player)))
        case None => DBIO.successful(NotFound)
      })
      database.run(select)
    })
}
