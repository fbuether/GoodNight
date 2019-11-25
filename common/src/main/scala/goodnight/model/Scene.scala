
package goodnight.model

import java.util.UUID


case class Scene(
  id: UUID,
  story: UUID,

  // the textual representation, uninterpreted.
  raw: String,

  // interpreted data, dependent on `raw`.

  // the (extracted) title and its url-representation
  title: String,
  urlname: String,
  // this is all non-setting text
  text: String,
  // the location, as in $ location = ...
  location: Option[UUID],
  // if this scene must happen as soon as possible, `$ mandatory`
  // to set, otherwise non-mandatory.
  mandatory: Boolean
)
