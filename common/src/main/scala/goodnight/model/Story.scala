
package goodnight.model


case class Story(
  creator: String, // refers User.name

  name: String,
  urlname: String,

  image: String,
  description: String,
  public: Boolean) // if this story is available to unregistered users as well.
