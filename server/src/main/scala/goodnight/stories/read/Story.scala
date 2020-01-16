
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

  // preparing read.Scenes.


  def asReadQuality(quality: db.model.Quality) = quality.sort match {
    case db.model.Sort.Bool =>
      model.read.Quality.Bool(quality.story,
        quality.urlname, quality.name, quality.image)
    case db.model.Sort.Integer =>
      model.read.Quality.Integer(quality.story,
        quality.urlname, quality.name, quality.image) }

  def asReadState(quality: model.read.Quality, value: String):
      model.read.State = quality match {
    case quality @ model.read.Quality.Bool(_,_,_,_) =>
      model.read.State.Bool(quality, value == "true")
    case quality @ model.read.Quality.Integer(_,_,_,_) =>
      model.read.State.Integer(quality, Try(value.toInt).getOrElse(0)) }


  def getFirstQuality(expr: model.Expression): Option[String] = expr match {
    case model.Expression.Text(n) => Some(n)
    case model.Expression.Number(_) => None
    case model.Expression.Unary(_, e) => getFirstQuality(e)
    case model.Expression.Binary(_, e1, e2) =>
      getFirstQuality(e1).orElse(getFirstQuality(e2))
  }

  val invalidDefaultQuality =
    model.read.Quality.Bool("invalid story",
    "invalid quality", "invalid quality", "X.png")

  def qualityOfName(qualities: Seq[db.model.Quality], name: String):
      model.read.Quality =
    qualities.find(_.urlname == name).map(asReadQuality).
      // this *should* never happen, as the test text is pre-typechecked.
      getOrElse(invalidDefaultQuality)

  def toReadChoice(story: db.model.Story, dbScene: db.model.Scene,
    qualities: Seq[db.model.Quality],
    state: Seq[(db.model.State, db.model.Quality)]):
      model.read.Choice = {

    val scene = SceneView.parse(story, dbScene)
    val requires = scene.settings.collect({
      case model.Setting.Require(e) => e })

    val tests = scene.settings.collect({
      case model.Setting.Require(expr) =>
        model.read.Test(
          qualityOfName(qualities, getFirstQuality(expr).getOrElse("")),
          Expression.evaluate(state, qualities, expr) match {
            case Some(model.Expression.Value.Bool(v)) => v
            case Some(model.Expression.Value.Integer(i)) => i > 0
            case _ => false
          },
          ExpressionPrinter.toTest(expr)) })

    model.read.Choice(scene.urlname,
      scene.text,
      tests.forall(_.succeeded),
      tests)
  }


  def asReadPlayerState(story: db.model.Story,
    qualities: Seq[db.model.Quality],
    player: db.model.Player,
    states: Seq[(db.model.State, db.model.Quality)],
    activity: db.model.Activity,
    scene: db.model.Scene,
    choices: Seq[db.model.Scene]
  ): model.read.PlayerState = {
    val parsed = SceneView.parse(story, scene)

    // (player, states, activity, scene)
    (model.read.Player(player.user, player.story, player.name),
      states.map(state => asReadState(asReadQuality(state._2), state._1.value)),
      model.read.Activity(player.story,
        player.user,
        activity.scene,
        parsed.settings.collect({ case model.Setting.Set(q, v) =>
          asReadState(qualityOfName(qualities, q),
            Expression.toString(
              Expression.evaluate(states, qualities, v))) })),
      model.read.Scene(scene.story,
        scene.urlname,
        scene.text,
        choices.map(toReadChoice(story, _, qualities, states))))
  }

  // type PlayerState = (model.Player, model.Activity, model.SceneView)
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
            Ok((toReadStory(story), state))))))
}
