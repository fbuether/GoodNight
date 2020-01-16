
package goodnight.stories.write

import java.util.UUID
import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database
import goodnight.parser.SceneParser


class Scenes(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  private def dbSceneOf(scene: model.Scene): db.model.Scene =
    db.model.Scene(UUID.randomUUID(),
      scene.story,
      scene.raw,
      scene.name,
      scene.urlname,
      scene.text)

  def createScene(storyUrlname: String) =
    // todo: check if the data actually got inserted.
    auth.SecuredAction.async(parse.text)(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
          SceneParser.parseScene(story.urlname, request.body) match {
            case Right(scene) =>
              val dbScene = dbSceneOf(scene)
              db.Scene.insert(dbScene).map(_ =>
                Accepted)
            case Left(error) =>
              DBIO.successful(Conflict(error))
          })))


  private def updateScene(oldScene: db.model.Scene, newScene: model.Scene) =
    db.model.Scene(oldScene.id,
      oldScene.story,
      newScene.raw,
      newScene.name,
      newScene.urlname,
      newScene.text)

  def saveScene(storyUrlname: String, sceneUrlname: String) =
    // todo: check if the data actually got inserted.
    auth.SecuredAction.async(parse.text)(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
          GetOrNotFound(db.Scene.named(storyUrlname, sceneUrlname)).
            flatMap(scene =>
              SceneParser.parseScene(story.urlname, request.body) match {
                case Right(newScene) if newScene.urlname == scene.urlname =>
                  db.Scene().insertOrUpdate(updateScene(scene, newScene)).
                    map(_ =>
                      Accepted)
                case Left(error) =>
                  DBIO.successful(Conflict(error))
              }))))
}
