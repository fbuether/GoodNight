
package goodnight.stories.write

import goodnight.db
import goodnight.model


object Convert {
  def edit(story: db.model.Story): model.edit.Story =
    model.edit.Story(story.urlname,
      story.name,
      story.image,
      story.public)

}
