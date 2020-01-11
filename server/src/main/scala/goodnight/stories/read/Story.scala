
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


  def asReadState(player: db.model.Player,
    states: Seq[(db.model.State, db.model.Quality)],
    activity: db.model.Activity,
    scene: db.model.Scene,
    choices: Seq[db.model.Scene]
  ): model.read.PlayerState = {

    def asReadQualityBool(quality: db.model.Quality):
        model.read.Quality[model.read.Sort.Bool.type] =
      model.read.Quality(quality.story,
        quality.urlname,
        model.read.Sort.Bool,
        quality.name,
        quality.image)

    def asReadQualityInt(quality: db.model.Quality):
        model.read.Quality[model.read.Sort.Integer.type] =
      model.read.Quality(quality.story,
        quality.urlname,
        model.read.Sort.Integer,
        quality.name,
        quality.image)

    def asReadState(state: db.model.State, quality: db.model.Quality):
        model.read.State = {
      quality.sort match {
        case db.model.Sort.Bool =>
          model.read.State(asReadQualityBool(quality),
            state.value == "true")
        case db.model.Sort.Integer =>
          model.read.State(asReadQualityInt(quality),
            Try(state.value.toInt).getOrElse(0))
      }
    }

    val effects = Seq() // ???

    def toReadChoice(scene: db.model.Scene): model.read.Choice =
      model.read.Choice(scene.urlname,
        scene.text,
        true,
        Seq() // ???
      )


    // (player, states, activity, scene)
    (model.read.Player(player.user, player.story, player.name),
      states.map(Function.tupled(asReadState)),
      model.read.Activity(player.story,
        player.user,
        activity.scene,
        effects),
      model.read.Scene(scene.story,
        scene.urlname,
        scene.text,
        choices.map(toReadChoice)))
  }

  // type PlayerState = (model.Player, model.Activity, model.SceneView)
  def loadPlayerState(story: db.model.Story, identity: Id):
      DBIO[Option[model.read.PlayerState]] =
    for (
      player <- DbOption(db.Player.ofStory(identity.user.name, story.urlname));
      states <- db.State.ofPlayer(identity.user.name, story.urlname);
      activity <- DbOption(db.Activity.newest(story.urlname,
        identity.user.name));
      scene <- DbOption(db.Scene.named(story.urlname, activity.scene));
      choices <- db.Scene.namedList(story.urlname,
        SceneView.getChoices(story, scene).toList))
    yield Some(asReadState(player, states, activity, scene, choices))


    // playerController.loadPlayer(identity.user.name, story.urlname).
    //   flatMap(playerStateOpt =>
    //     loadPlayerActivity(playerStateOpt).flatMap(playerActivityOpt =>
    //       toView(story, playerActivityOpt).map(sceneViewOpt =>

    //         playerStateOpt.flatMap(ps =>
    //           playerActivityOpt.flatMap(pa =>
    //             sceneViewOpt.map(sv =>
    //               {

    //                 val player = toReadPlayer(ps.player)
    //                 val

    //                 (player,
    //                   states,
    //                   activity,
    //                   scene)
    //               }
    //             ))))))

                  // (toReadPlayer(ps.player),
                  // Seq(),// ps.state.map(toReadState),
                  // toReadActivity(pa.activity),
                  // toReadScene(sv))))))))

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
            println(result)
            Ok(result)
          }))))
}
