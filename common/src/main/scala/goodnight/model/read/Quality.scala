
package goodnight.model.read


// Qualities have a type-parameter to statically assert their sort.
case class Quality[A <: Sort](
  story: String, // refers Story.urlname
  urlname: String, // refers edit.Quality.urlname

  sort: A,
  name: String, // the Quality's display name
  // hidden: Boolean,
  // order: Int, // the order in which qualities should be shown.
  // scene: Option[String], // refers Scene.urlname
  image: String)
  // description: String)
