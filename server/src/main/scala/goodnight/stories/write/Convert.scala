
package goodnight.stories.write

import java.util.UUID

import goodnight.db
import goodnight.model


object Convert {
  def editStory(story: db.model.Story): model.edit.Story =
    model.edit.Story(story.urlname,
      story.name,
      story.image,
      story.public)


  def editContent(story: db.model.Story,
    scenes: Seq[db.model.Scene],
    qualities: Seq[db.model.Quality]): model.edit.Content =
    model.edit.Content(
      editStory(story),
      scenes.map(scene => model.edit.SceneHeader(
        scene.urlname,
        scene.name,
        scene.text.take(80).trim + (if (scene.text.length > 80) "..." else ""),
        scene.text.contains("\n$ start") ||
        scene.text.contains("\n$start"))),
      qualities.map(quality => model.edit.QualityHeader(
        quality.urlname,
        quality.name,
        quality.description.take(80).trim +
          (if (quality.description.length > 80) "..." else ""))))


  def editScene(scene: db.model.Scene, prev: Seq[String], next: Seq[String]):
      model.edit.Scene =
    model.edit.Scene(
      scene.story,
      scene.urlname,
      scene.name,
      scene.raw,
      prev,
      next)


  def dbScene(scene: model.Scene) =
    db.model.Scene(UUID.randomUUID(), scene.story, scene.raw,
      scene.name, scene.urlname, scene.text)


  def editQuality(quality: db.model.Quality): model.edit.Quality =
    model.edit.Quality(quality.story,
      quality.urlname,
      quality.name,
      quality.raw)
}
