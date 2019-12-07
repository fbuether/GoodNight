
package goodnight.model


case class Choice(
  scene: String, // refers Scene.title

  // all values (and the existence of a choice) depend on `raw` of `scene`.

  // the index of this choice in relation to the scene itself, to order all
  // choices as intended.
  pos: Int,

  title: String,
  urlname: String,

  text: String
)


case class Scene(
  story: String, // refers Story.name

  // the textual representation, uninterpreted.
  raw: String,

  // interpreted data, dependent on `raw`.

  // the (extracted) title and its url-representation
  title: String,
  urlname: String,
  // this is all non-setting text
  text: String,
  // the location, as in $ location = ...
  location: Option[String], // refers Location.name
  // if this scene must happen as soon as possible, `$ mandatory`
  // to set, otherwise non-mandatory.
  mandatory: Boolean
)
