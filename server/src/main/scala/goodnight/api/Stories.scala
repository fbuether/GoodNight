
package goodnight.api

import java.util.UUID
import play.api.db.slick.DbName
import play.api.libs.functional.syntax._
import play.api.libs.json.{ JsValue, Json, Reads, JsPath, JsArray }
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.api.authentication.AuthService
import goodnight.api.authentication.Id
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database
import goodnight.logic.SceneParser

class Stories(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase


  case class NewSceneData(text: String)
  implicit val serialise_newSceneData: Serialisable[NewSceneData] = macroRW



  def createScene(storyName: String) =
    auth.SecuredAction.async(parseFromJson[NewSceneData])({ request =>
      val getStory = db.Story.ofUrlname(storyName)
      database.run(getStory).flatMap({
        case None =>
          Future.successful(
            NotFound("Story with this urlname does not exist."))
        case Some(story) =>
          SceneParser.parsePScene(request.body.text) match {
            case Left(error) =>
              Future.successful(UnprocessableEntity(ujson.Obj(
                "successful" -> false,
                "error" -> error)))
            case Right(SceneParser.PScene(content, settings, choices)) =>
              val newScene = model.Scene(UUID.randomUUID(),
                story.id,
                request.body.text,
                content.substring(0, content.length.min(20)), // todo
                urlnameOf(content.substring(0, content.length.min(20))), // todo
                content,
                None, // todo
                false) // todo

              val newChoices = choices.zipWithIndex.map({
                case (SceneParser.PChoice(content, settings), idx) =>
                  model.Choice(UUID.randomUUID(),
                    newScene.id,
                    idx,
                    content)
              })

              val insertScene = db.Scene().insert(newScene)
              val insertChoices = newChoices.map(db.Choice().insert)

              database.run(insertScene andThen DBIO.sequence(insertChoices)).
                map(_ => Created)
          }
      })
    })


  def updateScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(parseFromJson[NewSceneData])({ request =>
      val getScene = db.Scene.ofStory(storyUrlname, sceneUrlname)
      database.run(getScene).flatMap({
        case None =>
          Future.successful(
            NotFound("This Scene in this Story does not exist."))
        case Some(scene) =>
          SceneParser.parsePScene(request.body.text) match {
            case Right(SceneParser.PScene(content, settings, choices)) =>
              val updatedScene = scene.copy(
                raw = request.body.text,
                text = content)
              database.run(db.Scene().filter(_.id === scene.id).
                take(1).
                update(updatedScene)).flatMap(_ =>

                database.run(DBIO.sequence(
                  choices.zipWithIndex.map({
                    case (SceneParser.PChoice(content, settings), pos) =>
                    db.Choice.updateText(updatedScene.id, pos, content)
                  }))).flatMap(_ =>

                  database.run(
                    db.Choice().filter(choice =>
                      choice.scene === updatedScene.id &&
                        choice.pos > choices.length).
                      delete).map(_ =>

                Ok(updatedScene))))
            case Left(error) =>
              Future.successful(UnprocessableEntity(ujson.Obj(
                "successful" -> false,
                "error" -> error)))
          }
      })
    })


}
