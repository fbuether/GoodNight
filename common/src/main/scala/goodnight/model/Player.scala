
package goodnight.model


sealed trait State

object State {
  case class Location(
    location: Option[String] // refers Location.name
  ) extends State

  case class Scene(
    title: String // refers Scene.title
  ) extends State

  case class Choice(
    scene: String, // refers Choice.scene
    title: String // refers Choice.title
  ) extends State
}


case class Player(
  user: String, // refers User.name
  story: String, // refers Story.title
  name: String,
  state: State
)
