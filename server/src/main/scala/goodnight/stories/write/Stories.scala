
package goodnight.stories.write

import java.util.UUID
import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

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

  // todo: merge with sceneParser.urlnameOf
  def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase



  private def parseStory(user: model.User, name: String) =
    model.Story(UUID.randomUUID(),
      user.id,
      name,
      urlnameOf(name),
      "Moon.png",
      "",
      None)

  case class WithName(name: String)
  implicit val serialise_WithName: Serialisable[WithName] = macroRW

  def createStory =
    auth.SecuredAction.async(parseFromJson[WithName])(request =>
      database.run(
        EmptyOrConflict(db.Story.ofUrlname(urlnameOf(request.body.name))).
          andThen(db.Story.insert(
            parseStory(request.identity.user, request.body.name)).
            map(Ok(_)))))
}
