
package goodnight.stories.read

import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import play.api.mvc.Result

import goodnight.api.authentication.AuthService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.logic.SceneParser
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class Scenes(components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def toView(story: db.model.Story, dbScene: db.model.Scene):
      DBIO[model.SceneView] = {

    // todo: better handling of \r\n-s
    val scene = SceneParser.parseScene(story.model,
      dbScene.raw.replace("\r\n", "\n")
    ) match {
      case Left(a) =>
        println(dbScene.raw)
        println(a)
        dbScene.model
      case Right(r) => r
    }

    val nextNames = scene.settings.collect({ case model.Setting.Next(s) => s }).
      toList


    // DBIO.successful(

    db.Scene.namedList(story.urlname, nextNames).map({ nexts =>


      model.SceneView(
        scene.story,
        scene.urlname,
        scene.text,
        scene.settings.exists({
          case model.Setting.Return(_) => true
          case _ => false }),

        nexts.map(next =>
          model.NextScene(
            next.urlname,
            next.text,
            // todo: requirements.
            Seq()
          )
        )
          // model.NextScene(
          //   "nextscene-1-urlname",
          //   "nextscene-1-text",
          //   Seq(
          //     model.Requirement(
          //       model.Quality(
          //         "quality-1-story",
          //         "quality-1-raw",
          //         "quality-1-name",
          //         "quality-1-urlname",
          //         model.Sort.Integer(None,None),
          //         "Books.png",
          //         "quality-1-description"
          //       ),
          //       2,
          //       true,
          //       0.75d),
          //     model.Requirement(
          //       model.Quality(
          //         "quality-2-story",
          //         "quality-2-raw",
          //         "quality-2-name",
          //         "quality-2-urlname",
          //         model.Sort.Boolean,
          //         "Boxing Glove.png",
          //         "quality-2-description"
          //       ),
          //       1,
          //       false,
          //       1d))
          // ),
      )

    })


  }
  // def getAvailableScenes(storyUrlname: String, location: Option[String]) =
  //   auth.SecuredAction.async(request =>
  //     database.run(
  //       GetOrNotFound(db.Story.ofUrlname(storyUrlname)).flatMap({story =>
  //         val userId = request.identity.user.id
  //         GetOrNotFound(db.Player.ofStory(userId, story.id)).flatMap(player =>
  //           db.Scene.atLocation(story.id, player.stateRef).map(Ok(_)))
  //       })))

  // def getAllScenes(storyUrlname: String) =
  //   auth.SecuredAction.async(request =>
  //     database.run(
  //       db.Scene.allOfStory(storyUrlname).map(Ok(_))))

  // def doScene(storyUrlname: String, sceneUrlname: String) =
  //   auth.SecuredAction.async(request =>
  //   // todo: save the current player state to be at this scene.
  //     database.run(
  //       GetOrNotFound(db.Scene.ofStory(storyUrlname, sceneUrlname)).
  //         flatMap(scene =>
  //           db.Choice.ofScene(scene.id).map(choices =>
  //             Ok((scene, choices))))))

  // def doLocation(storyUrlname: String, locationUrlname: Option[String]) =
  //   auth.SecuredAction.async(request =>
  //     // todo: save the current player state to be at this location.
  //     Future.successful(Accepted))
}
