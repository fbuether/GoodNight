
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
import goodnight.model.{ Story, StoryTable }
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Stories(components: ControllerComponents,
  db: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase


  private def storiesFilterAuthorMyself(
    identity: Option[Id], filtersMyself: Boolean)(
    query: StoryTable.Q): StoryTable.Q = {
    println(s"well: $filtersMyself, $identity")
    (filtersMyself, identity) match {
      case (true, Some(ident)) => query.filter(_.creator === ident.user.id)
      case _ => query
    }
  }

  private def storiesFilterAuthor(author: Option[String])(
    query: StoryTable.Q): StoryTable.Q =
    author match {
      case Some(name) => StoryTable.filterCreator(name)(query)
      case _ => query
    }

  def showAll(filters: Map[String, Seq[String]]) =
    auth.UserAwareAction.async({request =>

      val query =
        storiesFilterAuthorMyself(request.identity,
          filters.get("authorMyself").isDefined)(
          storiesFilterAuthor(filters.get("author").map(_.head))(
            StoryTable()))

      db.run(query.result).map(sl => Ok(Json.toJson(sl)))
    })

  def showOne(reqName: String) = Action.async {
    val query = StoryTable().filter(_.urlname === reqName).result.headOption
    db.run(query).map(s => Ok(Json.toJson(s)))
  }

  case class StoryData(name: String)
  implicit val storyDataReads: Reads[StoryData] =
    (JsPath \ "name").read[String].map(StoryData(_))

  def create = auth.SecuredAction.async(parse.json)({ request =>
    parseJson[StoryData](request.body, { storyData =>
      val urlname = urlnameOf(storyData.name)
      val checkExistence = StoryTable().filter(s => s.urlname === urlname).
        take(1).result.headOption
      db.run(checkExistence).flatMap({
        case None =>
          val newStory = Story(UUID.randomUUID(),
            request.identity.user.id,
            storyData.name,
            urlname,
            "Moon.png",
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
