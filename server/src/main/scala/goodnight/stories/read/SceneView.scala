
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


object SceneView {
  def parse(story: db.model.Story, scene: db.model.Scene): model.Scene = {
    SceneParser.parseScene(story.model, scene.raw.replace("\r\n", "\n")) match {
      case Left(a) =>
        println(scene.raw)
        println(a)
        scene.model
      case Right(r) => r
    }
  }


  // extracts all scenes refered by next and include
  def getChoices(story: db.model.Story, dbScene: db.model.Scene): Seq[String] =
    parse(story, dbScene).settings.collect({
      case model.Setting.Next(scene) => scene
      case model.Setting.Include(scene) => scene })


  def ofScene(story: db.model.Story, dbScene: db.model.Scene)(
    implicit ec: ExecutionContext): DBIO[model.SceneView] = {

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

    val includes = scene.settings.
      collect({ case model.Setting.Include(s) => s}).toList
    db.Scene.namedList(story.urlname, includes).flatMap({ includedDbScenes =>
      val includedScenes = includedDbScenes.map(scene =>
        SceneParser.parseScene(story.model, scene.raw.replace("\r\n", "\n"))
          match {
          case Left(_) => scene.model
          case Right(r) => r
        })

      val text = scene.text + "\n" + String.join("\n",
        includedScenes.map(_.text) : _*)
      // no need to filter includes, as they will not be considered anymore.
      val settings = scene.settings ++ includedScenes.map(_.settings).flatten



      val nextNames = settings.collect({ case model.Setting.Next(s) => s }).
        toList

      db.Scene.namedList(story.urlname, nextNames).map(nexts =>
        model.SceneView(
          scene.story,
          scene.urlname,
          text,
          settings.exists({
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
      )
    })
  }
}
