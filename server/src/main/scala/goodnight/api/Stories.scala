
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


  def showAvailableScenes(storyUrlname: String) = auth.SecuredAction.async({
    request =>
    database.run(db.Story.ofUrlname(storyUrlname)).flatMap({
      case None =>
        Future.successful(
          NotFound("Story with this urlname does not exist."))
      case Some(story) =>
        database.run(db.Player.of(request.identity.user.id, story.id)).map({
          case None =>
            PreconditionFailed("User has not created a player yet.")
          case Some(player) =>
              Ok(ujson.Obj("options" -> true))
        })
    })
  })


  def showScenes(story: String) = auth.SecuredAction.async {
    val query = db.Scene().
      join(db.Story().filter(_.urlname === story)).on(_.story === _.id).
      map(_._1).
      result
    database.run(query).map(scenes => Ok(scenes))
  }

  case class StoryData(name: String)
  implicit val serialise_storyData: Serialisable[StoryData] = macroRW

  def create = auth.SecuredAction.async(parseFromJson[StoryData])({ request =>
    val urlname = urlnameOf(request.body.name)
    val checkExistence = db.Story().filter(s => s.urlname === urlname).
      take(1).result.headOption
    database.run(checkExistence).flatMap({
      case None =>
        val newStory = model.Story(UUID.randomUUID(),
          request.identity.user.id,
          request.body.name,
          urlname,
          "Moon.png",
          "",
          None)
        val insert = db.Story().insert(newStory)
        database.run(insert).map({ _ =>
          Ok(newStory)
        })
      case Some(_) =>
        Future.successful(Conflict(ujson.Obj(
          "successful" -> false,
          "error" -> "A story with the same reduced name already exists.")))
    })
  })

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
                content.substring(0, 20), // todo
                urlnameOf(content.substring(0, 20)), // todo
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
                    db.Choice.update(updatedScene.id, pos, content)
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


  private def createPlayerForStory(user: model.User, story: model.Story,
    playerName: String): Future[model.Player] = {
    val player = model.Player(UUID.randomUUID(),
      user.id,
      story.id,
      playerName,
      story.startLocation)
    database.run(db.Player().insert(player)).map(_ => player)
  }

  case class PlayerNameBody(name: String)
  implicit val serialise_playerNameBody: Serialisable[PlayerNameBody] = macroRW

  def createPlayer(storyUrlname: String) =
    auth.SecuredAction.async(parseFromJson[PlayerNameBody])({ request =>
      val playerName = request.body.name
      val user = request.identity.user
      database.run(db.Story.ofUrlname(storyUrlname)).flatMap({
        case None => notFound("Story not found.")
        case Some(story) =>
          createPlayerForStory(user, story, playerName).map(Created(_))
      })
    })
}
