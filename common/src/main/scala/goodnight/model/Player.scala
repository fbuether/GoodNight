
package goodnight.model


case class Player(
  user: String, // refers User.name
  story: String, // refers Story.urlname
  name: String,

  // this maps Quality.name -> value, where value may be a number as string
  state: Map[String, String]
)
