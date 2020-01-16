
package goodnight.stories.read

import java.util.UUID
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import scala.util.{ Try, Success, Failure }

import goodnight.server.DbOption
import goodnight.api.authentication.AuthService
import goodnight.api.authentication.Id
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database
import goodnight.printer.ExpressionPrinter


class Story(components: ControllerComponents,
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

  private def toReadStory(story: db.model.Story): model.read.Story =
    model.read.Story(story.urlname,
      story.name,
      story.creator,
      story.image)

  def getAvailableStories(query: Map[String, Seq[String]]) =
    auth.UserAwareAction.async(request =>
      database.run(
        getStoryQuery(request.identity, query.contains("myself"),
          // send only public stories if requested or not logged in
          query.contains("publicOnly") || !request.identity.isDefined).
          map(stories =>
            result[Seq[model.read.Story]](Ok, stories.map(toReadStory)))))


  def defaultActivity(story: db.model.Story, player: db.model.Player,
    defScene: String) =
    model.Activity(
      story.name,
      player.name,
      0,
      defScene,
      Seq(),
      Map())

  private def loadPlayerActivity(playerState: Option[db.model.PlayerState]):
      DBIO[Option[db.model.PlayerActivity]] =
    playerState match {
      case Some(state) => playerController.loadActivity(state.player)
      case None => DBIO.successful(None)
    }


  def asReadPlayerState(story: db.model.Story,
    qualities: Seq[db.model.Quality],
    player: db.model.Player,
    states: Seq[(db.model.State, db.model.Quality)],
    activity: db.model.Activity,
    scene: db.model.Scene,
    choices: Seq[db.model.Scene]): model.read.PlayerState =
    (Convert.read(player),
      states.map(state => Convert.read(Convert.read(state._2), state._1.value)),
      Convert.read(qualities, states, SceneView.parse(scene),
        activity),
      Convert.read(qualities, states, scene, choices))


  def loadPlayerState(story: db.model.Story, identity: Id):
      DBIO[Option[model.read.PlayerState]] =
    for (
      player <- DbOption(db.Player.ofStory(identity.user.name, story.urlname));
      qualities <- db.Quality.allOfStory(story.urlname);
      states <- db.State.ofPlayer(identity.user.name, story.urlname);
      activity <- DbOption(db.Activity.newest(story.urlname,
        identity.user.name));
      scene <- DbOption(db.Scene.named(story.urlname, activity.scene));
      choices <- db.Scene.namedList(story.urlname,
        SceneView.getChoices(story, scene).toList))
    yield Some(asReadPlayerState(story, qualities, player, states, activity,
      scene, choices))



  def withOptionalPlayerState(story: db.model.Story,
    identity: Option[Id], cont: Option[model.read.PlayerState] => Result):
      DBIO[Result] = identity match {
    case None => DBIO.successful(cont(None))
    case Some(identity) => loadPlayerState(story, identity).map(cont)
  }

  def getStory(urlname: String) =
    auth.UserAwareAction.async(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(urlname)).flatMap(story =>
          withOptionalPlayerState(story, request.identity, state =>
            result[model.read.StoryState](Ok, (toReadStory(story), state))))))
}
