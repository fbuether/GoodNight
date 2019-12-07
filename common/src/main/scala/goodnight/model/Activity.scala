
package goodnight.model


// a log of what a player has done in a story
case class Activity(
  story: String, // refers Story.name
  player: String, // refers Player.name

  // the position in the sequence of activities of this player
  number: Int,
  scene: String, // refers Scene.name

  // the effects that this activity had
  // this maps Quality.name -> value, where value may be a number as string
  effects: Map[String, String]
)
