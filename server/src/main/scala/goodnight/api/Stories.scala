
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


class Stories(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase


  private def storiesFilterAuthorMyself(
    identity: Option[Id], filtersMyself: Boolean)(
    query: db.Story.Q): db.Story.Q = {
    println(s"well: $filtersMyself, $identity")
    (filtersMyself, identity) match {
      case (true, Some(ident)) => query.filter(_.creator === ident.user.id)
      case _ => query
    }
  }

  private def storiesFilterAuthor(author: Option[String])(
    query: db.Story.Q): db.Story.Q =
    author match {
      case Some(name) => db.Story.filterCreator(name)(query)
      case _ => query
    }

  def showAll(filters: Map[String, Seq[String]]) =
    auth.UserAwareAction.async({request =>

      val query =
        storiesFilterAuthorMyself(request.identity,
          filters.get("authorMyself").isDefined)(
          storiesFilterAuthor(filters.get("author").map(_.head))(
            db.Story()))

      database.run(query.result).map(sl => Ok(sl))
    })

  def showOne(reqName: String) = auth.SecuredAction.async { request =>
    val getStory = db.Story().filter(_.urlname === reqName).result.headOption
    database.run(getStory).flatMap({
      case None =>
        Future.successful(NotFound)
      case Some(story) =>
        val getPlayer = db.Player().filter(player =>
          player.story === story.id &&
          player.user === request.identity.user.id).
          result.headOption
        database.run(getPlayer).map({ player =>
          Ok((story, player))
        })
    })
  }


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
          val newScene = model.Scene(UUID.randomUUID(),
            story.id,
            request.body.text,
            "title",
            "urlname",
            "image",
            None,
            "text",
            false)
          database.run(db.Scene().insert(newScene)).map({ _ =>
            Created
          })
      })
    })


  def updateScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(parseFromJson[NewSceneData])({ request =>
      val getStory = db.Story().filter(_.urlname === storyUrlname).
        result.headOption
      database.run(getStory).flatMap({
        case None =>
          Future.successful(
            NotFound("Story with this urlname does not exist."))
        case Some(story) =>
          val getScene = db.Scene().filter(scene =>
            scene.urlname === sceneUrlname &&
              scene.story === story.id).
            result.headOption
          database.run(getScene).flatMap({
            case None =>
              Future.successful(
                NotFound("Scene with this urlname does not exist."))
            case Some(scene) =>
              val updatedScene = scene.copy(raw = request.body.text)
              database.run(db.Scene().filter(_.id === scene.id).
                update(updatedScene)).map(_ => Ok)
          })
      })
    })



  case class PlayerNameBody(name: String)
  implicit val serialise_playerNameBody: Serialisable[PlayerNameBody] = macroRW

  def createPlayer(storyUrlname: String) =
    auth.SecuredAction.async(parseFromJson[PlayerNameBody])({ request =>
      val playerName = request.body.name
      val user = request.identity.user
      database.run(db.Story.ofUrlname(storyUrlname)).flatMap({
        case None =>
          Future.successful(NotFound(ujson.Obj(
            "success" -> false,
            "error" -> "Story with name \"" + storyUrlname +
              "\" does not exist.")))
        case Some(story) =>
          val newPlayer = model.Player(UUID.randomUUID(),
            user.id,
            story.id,
            playerName,
            story.startLocation)
          database.run(db.Player().insert(newPlayer)).map(_ =>
            Created(newPlayer))
      })
    })
}
