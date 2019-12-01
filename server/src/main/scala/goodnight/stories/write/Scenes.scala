
package goodnight.stories.write

import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database
import goodnight.logic.SceneParser


class Scenes(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  case class WithText(text: String)
  implicit val serialise_WithText: Serialisable[WithText] = macroRW

  // todo: check if the data actually got inserted.
  def createScene(storyUrlname: String) =
    auth.SecuredAction.async(parseFromJson[WithText])(request =>
      database.run(
        GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
          SceneParser.parseScene(story, request.body.text).fold(
            err => DBIO.successful(UnprocessableEntity(error(err))),
            parsed =>
            EmptyOrConflict(db.Scene.ofStory(storyUrlname, parsed._1.urlname)).
              andThen(
                db.Scene.insert(parsed._1).flatMap(insertedScene =>
                  DBIO.sequence(parsed._2.map(db.Choice.insert)).
                    map(_ => Created(insertedScene))))))))


  def replaceChoice(newChoice: model.Choice, oldChoices: Seq[model.Choice]) =
    oldChoices.filter(_.pos == newChoice.pos).headOption match {
      case Some(prev) => db.Choice.update(prev.id, newChoice)
      case None => db.Choice.insert(newChoice)
    }

  // todo: check if the data actually got updated.
  def updateScene(storyUrlname: String, sceneUrlname: String) =
    auth.SecuredAction.async(parseFromJson[WithText])(request =>
      database.run(for (
        story <- GetOrNotFound(db.Story.ofUrlname(storyUrlname));
        scene <- GetOrNotFound(db.Scene.ofStory(storyUrlname, sceneUrlname));
        choices <- db.Choice.ofScene(scene.id);
        result <- SceneParser.parseScene(story, request.body.text).fold(
          err => DBIO.successful(UnprocessableEntity(error(err))),
          parsed => for (
            _ <- db.Scene.update(scene.id, parsed._1);
            _ <- DBIO.sequence(parsed._2.
              map(_.copy(scene = scene.id)).
              map(replaceChoice(_, choices)))
          )
          yield Accepted))
      yield result))
      // database.run(
      //   GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap(story =>
      //     GetOrNotFound(db.Scene.ofStory(storyUrlname, sceneUrlname)).
      //       flatMap(scene =>
      //         SceneParser.parseScene(story, request.body.text).fold(
      //           err => DBIO.successful(UnprocessableEntity(error(err))),
      //           parsed =>
      //           db.Choice.ofScene(scene.id).flatMap(choices =>
      //             db.Scene.update(scene.id, parsed._1).flatMap(_ =>
      //               DBIO.sequence(parsed._2.
      //                 map(_.copy(scene = parsed._1.id)).
      //                 map(replaceChoice(_, choices))).
      //                 map(_ => Accepted))))))))
}
