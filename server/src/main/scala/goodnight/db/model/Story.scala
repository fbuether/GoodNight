
package goodnight.db.model

import java.util.UUID

import goodnight.db


case class Story(
  id: UUID,
  creator: String, // refers User.name

  name: String,
  urlname: String,

  image: String,
  description: String)
    extends DbModel {
  def model = goodnight.model.Story(creator, name, urlname, image, description)
}
