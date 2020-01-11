
package goodnight.db


package object model {
  type States = Seq[State]

  case class PlayerState(player: Player, state: States)

  case class PlayerActivity(activity: Activity, scene: Scene)
}
