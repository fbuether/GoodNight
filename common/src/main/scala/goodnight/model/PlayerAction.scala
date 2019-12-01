
package goodnight.model

import java.util.UUID

import goodnight.model


sealed trait Action
object Action {
  case class Scene(scene: model.Scene) extends Action
  case class Choice(choice: model.Choice) extends Action
  // self-directed movement requires a map-like view. Happens later.
  // for now, location is always None.
  case class Location(location: Option[model.Location]) extends Action
}


// PlayerState keeps a history of everything a player does.
// In this way, we can inspect her history and, if required, deduce what
// she did to lead her where she is now.
case class PlayerAction(
  id: UUID,

  // logical key: story, player, index
  story: UUID,
  player: UUID,

  // a monotonically increasing number for each player, to order their
  // activity sequentially.
  index: Int,
  action: Action
)
