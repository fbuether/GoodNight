
package goodnight.model.read


// a log of what a player has done in a story
case class Activity(
  story: String, // refers Player.story
  user: String, // refers Player.user
  scene: String, // refers Scene.name
  effects: States) // the effects that this activity had
