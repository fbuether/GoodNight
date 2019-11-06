
package goodnight.api

import java.util.UUID
import play.api.db.slick.DbName
import play.api.libs.functional.syntax._
import play.api.libs.json.{ JsValue, Json, Reads, JsPath }
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

import goodnight.api.authentication.AuthService
import goodnight.api.authentication.Id
import goodnight.common.api.Story._
import goodnight.common.api.Scene._
import goodnight.model.Story
import goodnight.model.Scene
import goodnight.db
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

      database.run(query.result).map(sl => Ok(Json.toJson(sl)))
    })

  def showOne(reqName: String) = auth.SecuredAction.async {
    val query = db.Story().filter(_.urlname === reqName).result.headOption
    database.run(query).map(s => Ok(Json.toJson(s)))
  }

  def showOneScene(story: String, scene: String) = auth.SecuredAction.async {
    val query = db.Scene().filter(_.urlname === scene).
      join(db.Story().filter(_.urlname === story)).on(_.story === _.id).
      map(_._1).
      result.headOption
    database.run(query).map(s => Ok(Json.toJson(s)))
  }

  case class StoryData(name: String)
  implicit val storyDataReads: Reads[StoryData] =
    (JsPath \ "name").read[String].map(StoryData(_))

  def create = auth.SecuredAction.async(parse.json)({ request =>
    parseJson[StoryData](request.body, { storyData =>
      val urlname = urlnameOf(storyData.name)
      val checkExistence = db.Story().filter(s => s.urlname === urlname).
        take(1).result.headOption
      database.run(checkExistence).flatMap({
        case None =>
          val newStory = Story(UUID.randomUUID(),
            request.identity.user.id,
            storyData.name,
            urlname,
            "Moon.png",
            "",
            None)
          val insert = db.Story().insert(newStory)
          database.run(insert).map({ _ =>
            Ok(Json.toJson(newStory))
          })
        case Some(_) =>
          Future.successful(Conflict(Json.obj(
            "successful" -> false,
            "error" -> "A story with the same reduced name already exists."
          )))
      })
    })})

  case class NewSceneData(text: String)
  implicit val newSceneDataReads: Reads[NewSceneData] =
    (JsPath \ "text").read[String].map(NewSceneData(_))

  def createScene(storyName: String) =
    auth.SecuredAction.async(parse.json)({ request =>
      parseJson[NewSceneData](request.body, { scene =>
        // val foo: String = db.Story().filter(_.urlname === storyName).result
        val getStory = db.Story().filter(_.urlname === storyName).
          result.headOption
        database.run(getStory).flatMap({
          case None =>
            Future.successful(
              NotFound("Story with this urlname does not exist."))
          case Some(story) =>
            val newScene = Scene(UUID.randomUUID(),
              story.id,
              scene.text,
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
    })
}
