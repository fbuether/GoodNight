
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
import goodnight.server.DbOption
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
      case Some(player) =>
        println("loading player and state!")
        db.State.ofPlayer(player.user, player.story).
          map(state => Some(db.model.PlayerState(player, state.map(_._1))))
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



  def getFirstScene(story: db.model.Story):
      DBIO[Option[db.model.Scene]] =
    // todo: use the actual first scene, or any of them if multiple.
    db.Scene.defaultOfStory(story.urlname)


  private def createNewPlayer(user: String, story: String, name: String):
      DBIO[db.model.Player] =
    db.Player.insert(db.model.Player(UUID.randomUUID(),
      user, story, name))


  def doCreatePlayer(user: db.model.User, storyUrlname: String, name: String):
      DBIO[Result] =
    for (
      story <- GetOrNotFound(db.Story.ofUrlname(storyUrlname));
      scene <- GetOrNotFound(getFirstScene(story));
      player <- createNewPlayer(user.name, story.urlname, name);
      qualities <- db.Quality.allOfStory(story.urlname);
      parsedScene <- DBIO.successful(SceneView.parse(scene));

      (activity, effects) <- Activity.go(user.name,
        qualities, Seq(), None, parsedScene);

      // todo: replace states with effects
      states <- db.State.ofPlayer(user.name, story.urlname);
      readScene <- SceneView.loadReadScene(
        qualities, states, story.urlname, scene.urlname))
    yield
        result[model.read.PlayerState](Created,
          (Convert.readPlayer(player),
            effects.map(Convert.readState(qualities, _)),
            Convert.readActivity(qualities, activity, effects),
            readScene))



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
