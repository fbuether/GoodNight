
package goodnight.stories.write

import java.util.UUID
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.parser.QualityParser
import goodnight.server.Controller
import goodnight.server.EmptyOrConflict
import goodnight.server.GetOrEither
import goodnight.server.PostgresProfile.Database


class Quality(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  def getQuality(storyUrlname: String, qualityUrlname: String) =
    auth.SecuredAction.async(request =>
      database.run(for (
        quality <- GetOrNotFound(db.Quality.named(storyUrlname,
          qualityUrlname)))
      yield result[model.edit.Quality](Ok, Convert.edit(quality))))


  def ofSort(sort: model.Sort): db.model.Sort = sort match {
    case model.Sort.Boolean => db.model.Sort.Bool
    case model.Sort.Integer(_,_) => db.model.Sort.Integer }

  def newQualityOfRaw(storyUrlname: String, raw: String):
      Either[String, db.model.Quality] =
    QualityParser.parse(storyUrlname, raw.replace("\r\n", "\n")).
      map(quality =>
        db.model.Quality(UUID.randomUUID(), quality.story, quality.raw,
          quality.name, quality.urlname, ofSort(quality.sort),
          quality.image, quality.description))


  def createQuality(storyUrlname: String) =
    auth.SecuredAction.async(parse.text)(request =>
      database.run(for (
        newQuality <- GetOrEither(BadRequest.apply : String => Result)(
          DBIO.successful(newQualityOfRaw(storyUrlname, request.body)));
        _ <- EmptyOrConflict(db.Quality.named(storyUrlname,
          newQuality.urlname));
        dbQuality <- db.Quality.insert(newQuality : db.model.Quality))
      yield result[model.edit.Quality](Accepted, Convert.edit(dbQuality))))


  def qualityOf(oldQuality: db.model.Quality, raw: String):
      Either[String, db.model.Quality] =
    QualityParser.parse(oldQuality.story, raw.replace("\r\n", "\n")).
      flatMap(quality =>
        if (quality.urlname != oldQuality.urlname)
          Left("The name of the quality must not be changed.")
        else
          Right(oldQuality.copy(
            raw = quality.raw,
            description = quality.description)))

  def saveQuality(storyUrlname: String, qualityUrlname: String) =
    auth.SecuredAction.async(parse.text)(request =>
      database.run(for (
        oldQuality <- GetOrNotFound(db.Quality.named(storyUrlname,
          qualityUrlname));
        newQuality <- GetOrEither(BadRequest.apply: String => Result)(
          DBIO.successful(qualityOf(oldQuality, request.body)));
        _ <- db.Quality.update(newQuality))
      yield result[model.edit.Quality](Accepted, Convert.edit(newQuality))))
}
