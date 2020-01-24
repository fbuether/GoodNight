
package goodnight.stories.read

import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import play.api.mvc.Result

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.parser.SceneParser
import goodnight.model
import goodnight.server.Controller
import goodnight.server.DbOption
import goodnight.server.PostgresProfile.Database


object SceneView {
  def parse(scene: db.model.Scene): model.Scene = {
      // todo: flatMap(typecheckScene(qualities, _))
    SceneParser.parseScene(scene.story, scene.raw.replace("\r\n", "\n")) match {
      case Left(a) =>
        println("*** Parsing a scene did fail:")
        println(scene.raw)
        println(a)
        scene.model
      case Right(r) => r
    }
  }


  private def constructReadChoice(qualities: Seq[db.model.Quality],
    state: Seq[(db.model.State, db.model.Quality)], scene: model.Scene):
      model.read.Choice = {
    val requires = scene.settings.collect({
      case model.Setting.Require(e) => e })
    val tests = scene.settings.collect({
      case model.Setting.Require(expr) =>
        Convert.readTest(qualities, state, expr) })

    model.read.Choice(scene.urlname,
      scene.text,
      tests.forall(_.succeeded),
      tests)
  }


  private def constructReadScene(qualities: Seq[db.model.Quality],
    state: Seq[(db.model.State, db.model.Quality)],
    scene: model.Scene,
    includes: Seq[model.Scene],
    choices: Seq[model.Scene]) =
    model.read.Scene(scene.story,
      scene.urlname,
      scene.text + "\n\n" + includes.map(_.text).mkString("\n\n"),
      choices.map(constructReadChoice(qualities, state, _)))


  private def getIncludes(scene: model.Scene) =
    scene.settings.collect({ case model.Setting.Include(scene) => scene })

  private def getChoices(scenes: Seq[model.Scene]) =
    scenes.map(_.settings).flatten.collect({
      case model.Setting.Next(scene) => scene })


  def loadReadScene(qualities: Seq[db.model.Quality],
    state: Seq[(db.model.State, db.model.Quality)],
    storyUrlname: String, sceneUrlname: String)(
    implicit ec: ExecutionContext):
      DBIO[model.read.Scene] =
    for (
      dbScene <- db.Scene.named(storyUrlname, sceneUrlname).map(_.get);
      scene <- DBIO.successful(parse(dbScene));
      includes <- db.Scene.namedList(storyUrlname, getIncludes(scene).toList).
        map(_.map(parse(_)));
      choices <- db.Scene.namedList(storyUrlname,
        getChoices(scene +: includes).toList).
        map(_.map(parse(_))))
    yield constructReadScene(qualities, state,
      scene, includes, choices)
}
