
package goodnight.model


case class Story(
  creator: String, // refers User.name
  name: String,
  urlname: String,
  image: String,
  description: String,
  // theme: String,
  startLocation: Option[String] // refers Location.name
)
