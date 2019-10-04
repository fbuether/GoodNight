
package goodnight.api

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.libs.json._
import play.api.libs.functional.syntax._

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile.Database
import play.api.db.slick.DbName

import goodnight.model.{ Story, StoryTable }
import goodnight.server.Controller




class Stories(components: ControllerComponents,
  db: Database)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase


  def showAll = Action.async {
    val query = StoryTable().map(story => (story.name, story.image)).result
    db.run(query).map({ data =>
      val reps = data.map({ case (name, image) =>
        Json.obj(
          "name" -> name,
          "image" -> image,
          "urlname" -> urlnameOf(name))
      })
      Ok(JsArray(reps))
    })
  }

  def showOne(reqName: String) = Action.async {
    val query = StoryTable().filter(_.urlname === reqName).result.headOption
    db.run(query).map({
      case Some(story) =>
        Ok(Json.obj(
          "name" -> story.name,
          "urlname" -> story.urlname,
          "image" -> story.image,
          "description" -> story.description))
      case None =>
        NotFound(Json.obj(
          "error" -> "Story not found."))
    })
  }
}
