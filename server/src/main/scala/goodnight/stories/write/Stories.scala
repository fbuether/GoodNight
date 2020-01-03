
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
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Stories(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  // todo: merge with sceneParser.urlnameOf
  def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase


  private def newStoryOfName(user: String, name: String) =
    db.model.Story(UUID.randomUUID(),
      user,
      name,
      urlnameOf(name),
      "Moon.png",
      "",
      false)

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
      database.run(
        db.Story.insert(newStoryOfName(request.identity.user.name,
          request.body)).flatMap(story =>
          db.Scene.insert(initialScene(story)).map(_ =>
            Created(story.model)))))


  def getContent(storyUrlname: String) =
    auth.SecuredAction.async(request =>
      database.run(for (
        scenes <- db.Scene.allOfStory(storyUrlname);
        qualities <- db.Quality.allOfStory(storyUrlname))
      yield Ok((scenes.map(_.model), qualities.map(_.model)))))

      // database.run(
      //   db.Scene.allOfStory(storyUrlname).flatMap(scenes =>
      //     db.Quality.allOfStory(storyUrlname).map(qualities =>
      //       Ok((scenes.map(_.model),
      //         qualities.map(_.model)))))))


    // auth.SecuredAction.async(request =>
    //   database.run(
    //     db.Scene.allOfStory(storyUrlname).flatMap(scenes =>
    //       db.Quality.allOfStory(storyUrlname).map(qualities =>
    //         Ok((scenes.map(_.model),
    //           qualities.map(_.model)))))))
}
