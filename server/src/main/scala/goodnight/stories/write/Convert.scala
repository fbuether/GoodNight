
package goodnight.stories.write

import goodnight.db
import goodnight.model


object Convert {
  def edit(story: db.model.Story): model.edit.Story =
    model.edit.Story(story.urlname,
      story.name,
      story.image,
      story.public)

  // def edit(): model.edit.Scene =
  //   model.edit.Scene(story,
  //     urlname,
  //     text)

  def edit(storyUrlname: String,
    scenes: Seq[db.model.Scene],
    qualities: Seq[db.model.Quality]): model.edit.Content =
    model.edit.Content(
      storyUrlname,
      scenes.map(scene => model.edit.SceneHeader(
        scene.urlname,
        scene.name,
        scene.text.take(80) + (if (scene.text.length > 80) "..." else ""),
        scene.text.contains("\n$ start") ||
        scene.text.contains("\n$start"))),
      qualities.map(quality => model.edit.QualityHeader(
        quality.urlname,
        quality.name,
        quality.description.take(80) +
          (if (quality.description.length > 80) "..." else ""))))
}
