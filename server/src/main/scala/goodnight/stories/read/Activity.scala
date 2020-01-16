
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
    scene: model.Scene)(implicit ec: ExecutionContext):
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
}
