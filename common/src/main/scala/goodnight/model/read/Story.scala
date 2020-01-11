
package goodnight.model.read


case class Story(
  urlname: String,

  name: String,
  creator: String, // refers User.name
  image: String)
