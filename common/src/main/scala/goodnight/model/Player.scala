
package goodnight.model


case class State(
  quality: String,
  value: String // this may be a number in string representation
)


case class Player(
  user: String, // refers User.name
  story: String, // refers Story.name
  name: String,

  // this maps Quality.name -> value, where value may be a number as string
  state: Map[String, String]
)
