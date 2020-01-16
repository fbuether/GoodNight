
package goodnight.stories.read

import java.util.UUID
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext

import goodnight.db
import goodnight.model


object Activity {
  def go(user: String, qualities: Seq[db.model.Quality],
    states: Seq[(db.model.State, db.model.Quality)],
    previous: Option[db.model.Activity],
    scene: model.Scene):
      DBIO[(db.model.Activity, db.model.States)] = {

    val effects: db.model.States =
      scene.settings.collect({ case model.Setting.Set(q, v) =>
        val state = states.find(_._1.quality == q).map(_._1)
        val value = Expression.toString(Expression.evaluate(
          states, qualities, v))
        state.map(_.copy(value = value)).
          getOrElse(db.model.State(
            UUID.randomUUID(),
            user,
            scene.story,
            q, value)) })
    val newActivity = db.model.Activity(
        UUID.randomUUID(),
        scene.story,
        user,
        previous.map(_.number + 1).getOrElse(0),
        scene.urlname,
        List())

    for (
      _ <- DBIO.sequence(effects.map(db.State().insertOrUpdate));
      activity <- db.Activity.insert(newActivity))
    yield (activity, effects)
  }



  // def doScene(player: db.model.Player, state: Seq[db.model.State],
  //   scene: db.model.Scene)(implicit ec: ExecutionContext):
  //     DBIO[(db.model.Activity, Seq[db.model.State])] = {

  //   // todo: verify that the user is actually currently able to do this scene
  //   // todo: check requirements as well as current scene of player

  //   db.Activity.newest(player.story, player.user).flatMap({ previous =>

  //     // todo: properly apply the scene's effect to the player.
  //     val activity = db.model.Activity(UUID.randomUUID(),
  //       scene.story,
  //       player.user,
  //       previous.map(_.number + 1).getOrElse(0),
  //       scene.urlname,
  //       List())
  //     val newState = state

  //     db.Activity.insert(activity).map(_ =>
  //       (activity, state))
  //   })

  // }


  // def createNewPlayer(user: db.model.User, story: db.model.Story,
  //   name: String)(implicit ec: ExecutionContext): DBIO[PlayerInfo] = {

  //   val newPlayer = db.model.Player(UUID.randomUUID(),
  //     user.name,
  //     story.urlname,
  //     name)
  //   val playerState = Seq()

  //   db.Player.insert(newPlayer).flatMap(player =>
  //     getFirstScene(story).flatMap(scene =>
  //       doScene(player, playerState, scene).map(as =>
  //         //            db.State.updateOrInsert(as._2).flatMap(playerState =>
  //         (newPlayer, playerState,
  //           as._1,
  //           scene))))

  // }
}
