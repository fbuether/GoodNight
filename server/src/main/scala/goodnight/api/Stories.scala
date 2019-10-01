
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
    name.trim


  def showAll = Action.async {
    val query = StoryTable().map(story => (story.name, story.image)).result
    val data: Future[Seq[(String, String)]] = db.run(query)
    val result = data.map(data =>
      Json.arr(
        data.map({ case (name, image) =>
          Json.obj(
            "name" -> name,
            "image" -> image,
            "urlname" -> urlnameOf(name))
        })))
    result.map(Ok(_))
  }
}