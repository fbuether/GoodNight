
package goodnight.stories.write

import java.util.UUID
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW
import scala.util.{ Either, Left, Right }

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.parser.SceneParser
import goodnight.parser.ExpressionTypechecker
import goodnight.printer.ExpressionPrinter
import goodnight.server.Controller
import goodnight.server.EmptyOrConflict
import goodnight.server.GetOrEither
import goodnight.server.PostgresProfile.Database
import goodnight.stories.read.SceneView


class Scene(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  def getScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(request =>
      database.run(for (
        scene <- GetOrNotFound(db.Scene.named(storyUrlname, sceneUrlname));
        prev <- db.SceneReference.prevAsStrings(storyUrlname, sceneUrlname);
        next <- db.SceneReference.nextAsStrings(storyUrlname, sceneUrlname))
      yield result[model.edit.Scene](Ok, Convert.editScene(scene, prev, next))))


  def parseScene(qualities: Seq[db.model.Quality],
    storyUrlname: String, raw: String):
      Either[String, model.Scene] =
    SceneParser.parseScene(storyUrlname, raw.replace("\r\n", "\n")).
      flatMap(typecheckScene(qualities, _))



  def getReferencesOfScene(scene: model.Scene): Seq[(String, String)] =
    scene.settings.collect({
      case model.Setting.Next(scene) => ("next", scene)
      case model.Setting.Include(scene) => ("include", scene) })

  def createScene(storyUrlname: String) =
    auth.SecuredAction.async(parse.text)(request =>
      database.run(for (
        qualities <- db.Quality.allOfStory(storyUrlname);
        newScene <- GetOrEither(BadRequest.apply : String => Result)(
          DBIO.successful(parseScene(qualities, storyUrlname, request.body)));
        _ <- EmptyOrConflict(db.Scene.named(storyUrlname, newScene.urlname));
        dbScene <- db.Scene.insert(Convert.dbScene(newScene));
        _ <- db.SceneReference.write(storyUrlname, newScene.urlname,
          getReferencesOfScene(newScene));
        prev <- db.SceneReference.prevAsStrings(storyUrlname, newScene.urlname);
        next <- db.SceneReference.nextAsStrings(storyUrlname, newScene.urlname))
      yield result[model.edit.Scene](Accepted, Convert.editScene(dbScene,
        prev, next))))


  def typecheckScene(qualities: Seq[db.model.Quality], scene: model.Scene):
      Either[String, model.Scene] = {
    val context = qualities.map(q => (q.urlname, q.sort match {
      case db.model.Sort.Bool => model.Expression.Type.Bool
      case db.model.Sort.Integer => model.Expression.Type.Int })).toMap

    val checks = scene.settings.collect({
      case model.Setting.Set(q, e) =>
        // check like a comparison, has the same typing characteristics.
        ExpressionTypechecker.check(context,
          model.Expression.Binary(model.Expression.Equal,
            model.Expression.Text(q), e)).
          flatMap(_ => Right(true))
      case model.Setting.Test(e) =>
        ExpressionTypechecker.check(context, e).
          flatMap({
            case model.Expression.Type.Bool => Right(true)
            case model.Expression.Type.Int =>
              Left("The test \"" +
                ExpressionPrinter.print(e) + "\" must be a boolean value, " +
                "but is a number.") })
      case model.Setting.Require(e) =>
        ExpressionTypechecker.check(context, e).
          flatMap({
            case model.Expression.Type.Bool => Right(true)
            case model.Expression.Type.Int =>
              Left("The requirement \"" +
                ExpressionPrinter.print(e) + "\" must be a boolean value, " +
                "but is a number.") })
    })

    checks.foldLeft(Right(scene) : Either[String,model.Scene])((s,e) =>
      e.flatMap(_ => s))
  }

  def updateWith(oldScene: db.model.Scene, newScene: model.Scene):
      Either[String, db.model.Scene] =
    if (newScene.urlname != oldScene.urlname)
      Left("The name of the scene must not be changed.")
    else
      Right(oldScene.copy(
        raw = newScene.raw,
        text = newScene.text))

  def saveScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(parse.text)(request =>
      database.run(for (
        qualities <- db.Quality.allOfStory(storyUrlname);
        parsedScene <- GetOrEither(BadRequest.apply : String => Result)(
          DBIO.successful(parseScene(qualities, storyUrlname, request.body)));
        oldScene <- GetOrNotFound(db.Scene.named(storyUrlname, sceneUrlname));
        newScene <- GetOrEither(BadRequest.apply : String => Result)(
          DBIO.successful(updateWith(oldScene, parsedScene)));
        _ <- db.Scene.update(newScene);
        _ <- db.SceneReference.write(storyUrlname, newScene.urlname,
          getReferencesOfScene(parsedScene));
        prev <- db.SceneReference.prevAsStrings(storyUrlname, newScene.urlname);
        next <- db.SceneReference.nextAsStrings(storyUrlname, newScene.urlname))
      yield result[model.edit.Scene](Accepted, Convert.editScene(newScene,
        prev, next))))
}
