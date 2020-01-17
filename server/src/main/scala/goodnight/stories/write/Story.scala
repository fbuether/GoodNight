
package goodnight.stories.write

import java.util.UUID
import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.model
import goodnight.server.DbOption
import goodnight.server.Controller
import goodnight.server.EmptyOrConflict
import goodnight.server.PostgresProfile.Database


class Story(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  private def newStoryOfName(user: String, name: String)(
    implicit ec: ExecutionContext):
      Option[db.model.Story] =
    if (name.trim.length <= 0) None
    else Some(db.model.Story(UUID.randomUUID(),
      user, name.trim, goodnight.urlnameOf(name.trim),
      "Moon.png", "", false))

  private def initialScene(story: db.model.Story) =
    db.model.Scene(UUID.randomUUID(),
      story.urlname,
      """|$start
         |$name: First Scene
         |This is a first scene for your new story.
         |Change this scene to be the start of your story, or remove it,
         |and create scenes as you like. Be aware though, that you always
         |need one starting scene! (as given by $start).""".stripMargin,
      "First Scene",
      "first-scene",
      """This is a first scene for your new story.
         |Change this scene to be the start of your story, or remove it,
         |and create scenes as you like. Be aware though, that you always
         |need one starting scene! (as given by $start).""".stripMargin)


  def createStory =
    auth.SecuredAction.async(parse.text)(request =>
      // todo: check if the user is allowed to create stories.
      database.run(for (
        editStory <- GetOr(BadRequest)(DBIO.successful(newStoryOfName(
          request.identity.user.name, request.body)));
        _ <- EmptyOrConflict(db.Story.ofUrlname(editStory.urlname));
        dbStory <- db.Story.insert(editStory);
        dbScene <- db.Scene.insert(initialScene(dbStory)))
      yield result[(model.edit.Story, model.edit.Content)](Created,
        (Convert.edit(dbStory),
          Convert.edit(dbStory.urlname, Seq(dbScene), Seq())))))



  def getContent(storyUrlname: String) =
    auth.SecuredAction.async(request =>
      database.run(for (
        scenes <- db.Scene.allOfStory(storyUrlname);
        qualities <- db.Quality.allOfStory(storyUrlname))
      yield result[model.edit.Content](Ok,
        Convert.edit(storyUrlname, scenes, qualities))))
}
