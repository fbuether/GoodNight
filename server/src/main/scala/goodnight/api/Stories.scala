
package goodnight.api

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.libs.json.{ JsValue, Json, Reads, JsPath }
import play.api.libs.functional.syntax._

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import goodnight.api.authentication.JwtEnvironment

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile.Database
import play.api.db.slick.DbName

import goodnight.model.{ Story, StoryTable }
import goodnight.server.Controller

import goodnight.common.api.Story._


class Stories(components: ControllerComponents,
  db: Database,
  silhouette: Silhouette[JwtEnvironment])(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase


  def showAll = Action.async {
    val query = StoryTable().result
    db.run(query).map(sl => Ok(Json.toJson(sl)))
  }

  def showOne(reqName: String) = Action.async {
    val query = StoryTable().filter(_.urlname === reqName).result.headOption
    db.run(query).map(s => Ok(Json.toJson(s)))
  }

  case class StoryData(name: String)
  implicit val storyDataReads: Reads[StoryData] =
    (JsPath \ "name").read[String].map(StoryData(_))

  def create = silhouette.SecuredAction.async(parse.json)({ request =>
    parseJson[StoryData](request.body, { storyData =>
      val urlname = urlnameOf(storyData.name)
      val checkExistence = StoryTable().filter(s => s.urlname === urlname).
        take(1).result.headOption
      db.run(checkExistence).flatMap({
        case None =>
          val newStory = Story(UUID.randomUUID(),
            request.identity.id,
            storyData.name,
            urlname,
            "",
            "",
            None)
          val insert = StoryTable().insert(newStory)
          db.run(insert).map({ _ =>
            Ok(Json.toJson(newStory))
          })
        case Some(_) =>
          Future.successful(Conflict(Json.obj(
            "successful" -> false,
            "error" -> "A story with the same reduced name already exists."
          )))
      })
    })})
}
