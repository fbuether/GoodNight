
package goodnight.db.model

import java.util.UUID

sealed trait Sort
object Sort {
  case object Bool extends Sort
  case object Integer extends Sort
}


case class Quality (
  id: UUID,
  story: String,
  raw: String,
  name: String,
  urlname: String,
  sort: Sort,
  image: String,
  description: String)
    extends DbModel {
  // def model = goodnight.model.Quality(
  //   story, raw, name, urlname,
  //   goodnight.model.Sort.Boolean, // todo.
  //   image, description)
}
