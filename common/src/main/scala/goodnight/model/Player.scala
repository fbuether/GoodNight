
package goodnight.model


case class Player(
  user: String, // refers User.name
  story: String, // refers Story.urlname
  name: String,

  // maps each Quality the player possesses to the level she has of it
  state: Map[Quality, String]
)
