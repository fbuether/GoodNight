
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
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  type PlayerState = (db.model.Player, Seq[db.model.State])
  // type PlayerActivity = (db.model.Activity, db.model.Scene)

  private def withState(playerOpt: Option[db.model.Player]):
      DBIO[Option[PlayerState]] =
    playerOpt match {
      case Some(player) => db.State.ofPlayer(player.user, player.story).
          map(state => Some(player, state))
      case None => DBIO.successful(None)
    }

  def loadPlayer(user: String, story: String): DBIO[Option[PlayerState]] =
    db.Player.ofStory(user, story).flatMap(withState)


  private def playerOf(user: db.model.User, story: db.model.Story,
    name: String) =
    db.model.Player(UUID.randomUUID(),
      user.name,
      story.urlname,
      name)

  private def getFirstScene(story: db.model.Story) =
    db.Scene.defaultOfStory(story.urlname)



  case class WithName(name: String)
  implicit val serialise_WithName: Serialisable[WithName] = macroRW

  def createPlayer(storyUrlname: String) =
    auth.SecuredAction.async(parseFromJson[WithName])(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
          // todo: verify that this user does not yet have a player for
          // this story.
          GetOrNotFound(db.Scene.defaultOfStory(story.urlname)).flatMap(scene =>
            db.Player.insert(playerOf(request.identity.user, story,
              request.body.name)).map({ player =>
                // todo: fetch player state as given by first scene.
                val playerState = Seq()
                Created((player.model(playerState),
                  model.Activity(story.name, request.identity.user.name,
                    0, scene.name,
                    // todo: well.
                    List(), Map()),
                  scene.model))
              })))))
}
