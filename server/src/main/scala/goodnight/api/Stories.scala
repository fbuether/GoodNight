
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

import goodnight.common.api.Story._


class Stories(components: ControllerComponents,
  db: Database)(
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
}
