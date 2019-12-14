
package goodnight.db.model

import java.util.UUID


case class Scene (
  id: UUID,
  story: String,
  raw: String,
  name: String,
  urlname: String,
  text: String,
//  settings: Seq[Setting]
)
    extends DbModel {
  def model = goodnight.model.Scene(
    story, raw, name, urlname, text,
    Seq()) // todo.
}
