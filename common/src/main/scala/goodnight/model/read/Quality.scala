
package goodnight.model.read


sealed trait Quality {
  def story: String // refers Story.urlname
  def urlname: String // refers edit.Quality.urlname

  def sort: Sort
  def name: String // the Quality's display name
  // hidden: Boolean
  // order: Int // the order in which qualities should be shown.
  // scene: Option[String] // refers Scene.urlname
  def image: String
  // description: String
}
object Quality {
  @upickle.implicits.key("Quality.Bool")
  case class Bool(story: String, urlname: String, name: String,
    image: String)
      extends Quality {
    val sort = Sort.Bool
  }

  @upickle.implicits.key("Quality.Integer")
  case class Integer(story: String, urlname: String, name: String,
    image: String)
      extends Quality {
    val sort = Sort.Integer
  }

  def apply(story: String,
    urlname: String,
    sort: Sort,
    name: String,
    image: String): Quality =
    sort match {
      case Sort.Bool => Bool(story, urlname, name, image)
      case Sort.Integer => Integer(story, urlname, name, image)
    }
}
