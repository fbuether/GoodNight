
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
          map(stories => Ok(stories.map(toReadStory)))))


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

  private def toView(story: db.model.Story,
    pa: Option[db.model.PlayerActivity]): DBIO[Option[model.SceneView]] =
    pa match {
      case Some(activity) =>
        SceneView.ofScene(story, activity.scene).map(Some.apply)
      case None => DBIO.successful(None)
    }


  def toReadPlayer(player: db.model.Player): model.read.Player =
    model.read.Player(player.user,
      player.story,
      player.name)

  // def toReadState(state: db.model.State): model.read.State =
  //   model.read.State(


  def toReadActivity(activity: db.model.Activity) =
    model.read.Activity(activity.story,
      activity.user,
      activity.scene,
      Seq() // todo: effects
    )

  def toReadScene(scene: model.SceneView) =
    model.read.Scene(scene.story,
      scene.urlname,
      scene.text,
      Seq() // todo: choices
    )


  // type PlayerState = (model.Player, model.Activity, model.SceneView)
  def loadPlayerState(story: db.model.Story, identity: Id):
      DBIO[Option[model.read.PlayerState]] =
    playerController.loadPlayer(identity.user.name, story.urlname).
      flatMap(playerStateOpt =>
        loadPlayerActivity(playerStateOpt).flatMap(playerActivityOpt =>
          toView(story, playerActivityOpt).map(sceneViewOpt =>

            playerStateOpt.flatMap(ps =>
              playerActivityOpt.flatMap(pa =>
                sceneViewOpt.map(sv =>
                  (toReadPlayer(ps.player),
                  Seq(),// ps.state.map(toReadState),
                  toReadActivity(pa.activity),
                  toReadScene(sv))))))))

                  // (Player, Seq[State], Activity, Scene)

                  // (ps._1.model(ps._2), pa._1.model, sv)))))))


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
          withOptionalPlayerState(story, request.identity, { state =>
            val result: model.read.StoryState = (toReadStory(story), state)
            Ok(result)
          }))))
}
