
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
import goodnight.server.PostgresProfile.Database


object SceneView {
  def parse(scene: db.model.Scene): model.Scene = {
    SceneParser.parseScene(scene.story, scene.raw.replace("\r\n", "\n")) match {
      case Left(a) =>
        println("*** Parsing a scene did fail:")
        println(scene.raw)
        println(a)
        scene.model
      case Right(r) => r
    }
  }

  // extracts all scenes refered by next and include
  def getChoices(story: db.model.Story, dbScene: db.model.Scene): Seq[String] =
    parse(dbScene).settings.collect({
      case model.Setting.Next(scene) => scene
      case model.Setting.Include(scene) => scene })
}
