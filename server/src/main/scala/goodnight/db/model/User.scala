
package goodnight.db.model

import java.util.UUID


// todo: generate by macro from goodnight.model.User
case class User(
  id: UUID,
  name: String)
    extends DbModel {
  def model = goodnight.model.User(name)
}
