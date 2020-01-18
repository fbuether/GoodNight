
package goodnight.stories.write

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
import goodnight.parser.SceneParser
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
        scene <- GetOrNotFound(db.Scene.named(storyUrlname, sceneUrlname)))
      yield result[model.edit.Scene](Ok, Convert.edit(scene))))


  def newSceneOfRaw(storyUrlname: String, raw: String):
      Either[String, db.model.Scene] =
    SceneParser.parseScene(storyUrlname, raw.replace("\r\n", "\n")).
      map(scene =>
        db.model.Scene(UUID.randomUUID(), scene.story, scene.raw,
          scene.name, scene.urlname, scene.text))


  def createScene(storyUrlname: String) =
    auth.SecuredAction.async(parse.text)(request =>
      database.run(for (
        newScene <- GetOrEither(BadRequest.apply : String => Result)(
          DBIO.successful(newSceneOfRaw(storyUrlname, request.body)));
        _ <- EmptyOrConflict(db.Scene.named(storyUrlname, newScene.urlname));
        dbScene <- db.Scene.insert(newScene))
      yield result[model.edit.Scene](Ok, Convert.edit(dbScene))))


  def sceneOf(oldScene: db.model.Scene, raw: String):
      Either[String, db.model.Scene] =
    SceneParser.parseScene(oldScene.story, raw.replace("\r\n", "\n")).
      flatMap(scene =>
        if (scene.urlname != oldScene.urlname)
          Left("The name of the scene must not be changed.")
        else
          Right(oldScene.copy(
            raw = scene.raw,
            text = scene.text)))

  def saveScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(parse.text)(request =>
      database.run(for (
        oldScene <- GetOrNotFound(db.Scene.named(storyUrlname, sceneUrlname));
        newScene <- GetOrEither(BadRequest.apply: String => Result)(
          DBIO.successful(sceneOf(oldScene, request.body)));
        _ <- db.Scene.update(newScene))
      yield result[model.edit.Scene](Ok, Convert.edit(newScene))))
}
