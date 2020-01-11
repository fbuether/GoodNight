
package goodnight.stories.read

import java.util.UUID
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.api.authentication.AuthService
import goodnight.api.authentication.SignUp
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Player(components: ControllerComponents,
  database: Database,
  auth: AuthService,
  signUp: SignUp)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  private def withState(playerOpt: Option[db.model.Player]):
      DBIO[Option[db.model.PlayerState]] =
    playerOpt match {
      case Some(player) => db.State.ofPlayer(player.user, player.story).
          map(state => Some(db.model.PlayerState(player, state)))
      case None => DBIO.successful(None)
    }

  def loadPlayer(user: String, story: String):
      DBIO[Option[db.model.PlayerState]] =
    db.Player.ofStory(user, story).flatMap(withState)



  def loadActivity(player: db.model.Player):
      DBIO[Option[db.model.PlayerActivity]] =
    db.Activity.newest(player.story, player.user).flatMap({
      case None => DBIO.successful(None)
      case Some(activity) =>
        db.Scene.named(player.story, activity.scene).map(sceneOpt =>
          sceneOpt.map(scene => db.model.PlayerActivity(activity, scene)))
    })


  def doCreatePlayer(user: db.model.User, storyUrlname: String, name: String):
      DBIO[Result] =
    GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
      GetOrNotFound(db.Scene.defaultOfStory(story.urlname)).flatMap(scene =>
        Activity.createNewPlayer(user, story, name).flatMap(pi =>
          SceneView.ofScene(story, pi._4).map(sceneView =>
            // todo: fetch player state as given by first scene.
            ???))))
            // Created((pi._1.model(pi._2),
            //   pi._3.model,
            //   sceneView))))))


  case class WithName(name: String)
  implicit val serialise_WithName: Serialisable[WithName] = macroRW

  def createPlayer(storyUrlname: String) =
    auth.SecuredAction.async(parseFromJson[WithName])(request =>
      database.run(
        // todo: verify that this user does not yet have a player for
        // this story.
        doCreatePlayer(request.identity.user, storyUrlname,
          request.body.name)))


  def createTemporary(storyUrlname: String) =
    auth.UnsecuredAction.async(request =>
      database.run(
        signUp.createTemporaryUser(request, user =>
          // see also: client/.../TemporaryPlayer.scala for the name.
          doCreatePlayer(user, storyUrlname, "Mrs. Hollywoockle"))))
}
