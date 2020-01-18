
package goodnight.stories.write

import goodnight.db
import goodnight.model


object Convert {
  def edit(story: db.model.Story): model.edit.Story =
    model.edit.Story(story.urlname,
      story.name,
      story.image,
      story.public)


  def edit(storyUrlname: String,
    scenes: Seq[db.model.Scene],
    qualities: Seq[db.model.Quality]): model.edit.Content =
    model.edit.Content(
      storyUrlname,
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


  def edit(scene: db.model.Scene): model.edit.Scene =
    model.edit.Scene(
      scene.story,
      scene.urlname,
      scene.name,
      scene.raw)


  def edit(quality: db.model.Quality): model.edit.Quality =
    model.edit.Quality(quality.story,
      quality.urlname,
      quality.name,
      quality.raw)
}
