
package goodnight.stories.read

import java.util.UUID
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext


import goodnight.db


object Activity {
  type PlayerInfo = (db.model.Player, Seq[db.model.State],
    db.model.Activity,
    db.model.Scene)


  def getFirstScene(story: db.model.Story)(implicit ec: ExecutionContext):
      DBIO[db.model.Scene] =
    // todo: handle if no default scene exists properly
    db.Scene.defaultOfStory(story.urlname).map(_.get)


  def doScene(player: db.model.Player, state: Seq[db.model.State],
    scene: db.model.Scene): DBIO[(db.model.Activity, Seq[db.model.State])] = {
    // todo: properly apply the scene's effect to the player.
    val activity = db.model.Activity(UUID.randomUUID(),
      scene.story,
      player.user,
      0,
      scene.urlname,
      List())
    val newState = state

    DBIO.successful((activity, state))
  }


  def createNewPlayer(user: db.model.User, story: db.model.Story,
    name: String)(implicit ec: ExecutionContext): DBIO[PlayerInfo] = {

    val newPlayer = db.model.Player(UUID.randomUUID(),
      user.name,
      story.urlname,
      name)
    val playerState = Seq()

    db.Player.insert(newPlayer).flatMap(player =>
      getFirstScene(story).flatMap(scene =>
        doScene(player, playerState, scene).flatMap(as =>
          db.Activity.insert(as._1).map(activity =>
//            db.State.updateOrInsert(as._2).flatMap(playerState =>
            (newPlayer, playerState,
              activity,
              scene)))))

  }
}
