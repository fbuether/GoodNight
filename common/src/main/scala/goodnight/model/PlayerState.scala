
package goodnight.model

import java.util.UUID

// PlayerState keeps a history of everything a player does.
// In this way, we can inspect her history and, if required, deduce what
// she did to lead her where she is now.
case class PlayerState(
  id: UUID,

  // logical key: story, player, index
  story: UUID,
  player: UUID,
  // a monotonically increasing number for each player, to order their
  // activity sequentially.
  index: Int,

  // each activity is connected to a scene
  scene: UUID,



  // store random state of last action.
)
