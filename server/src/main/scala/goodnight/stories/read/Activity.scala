
package goodnight.stories.read

import java.util.UUID
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext

import goodnight.db
import goodnight.model


object Activity {
  type PlayerInfo = (db.model.Player, Seq[db.model.State],
    db.model.Activity,
    db.model.Scene)


  def getFirstScene(story: db.model.Story)(implicit ec: ExecutionContext):
      DBIO[db.model.Scene] =
    // todo: handle if no default scene exists properly
    db.Scene.defaultOfStory(story.urlname).map(_.get)


  def doScene(player: db.model.Player, state: Seq[db.model.State],
    scene: db.model.Scene)(implicit ec: ExecutionContext):
      DBIO[(db.model.Activity, Seq[db.model.State])] = {

    // todo: verify that the user is actually currently able to do this scene
    // todo: check requirements as well as current scene of player

    db.Activity.newest(player.story, player.user).flatMap({ previous =>

      // todo: properly apply the scene's effect to the player.
      val activity = db.model.Activity(UUID.randomUUID(),
        scene.story,
        player.user,
        previous.map(_.number + 1).getOrElse(0),
        scene.urlname,
        List())
      val newState = state

      db.Activity.insert(activity).map(_ =>
        (activity, state))
    })

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
        doScene(player, playerState, scene).map(as =>
          //            db.State.updateOrInsert(as._2).flatMap(playerState =>
          (newPlayer, playerState,
            as._1,
            scene))))

  }


  def evaluateTest(states: Seq[(db.model.State, db.model.Quality)],
    qualities: Seq[db.model.Quality],
    expr: model.Expression):
      Option[model.Expression.Value] = {
    import goodnight.model.Expression._
    import goodnight.model.Expression.Value._
    expr match {
      case Text(name) =>
        // we have to infer the type depending on the referred quality
        states.find(_._2.urlname == name).
          map(state => (state._2.sort, state._1.value)) match {
            case Some((db.model.Sort.Bool, v)) => Some(Bool(v == "true"))
            case Some((db.model.Sort.Integer, v)) =>
              try { Some(Integer(v.toInt)) } catch { case _: Exception => None }
            case None => qualities.find(_.urlname == name).map(_.sort) match {
              case Some(db.model.Sort.Bool) => Some(Bool(false))
              case Some(db.model.Sort.Integer) => Some(Integer(0))
              case _ => None } }
      case Number(n) => Some(Integer(n))
      case Unary(Not, e) => evaluateTest(states, qualities, e) match {
        case Some(Bool(b)) => Some(Bool(!b))
        case Some(Integer(_)) => None
        case None => None }
      case Binary(op, e1, e2) =>
        val v1 = evaluateTest(states, qualities, e1)
        val v2 = evaluateTest(states, qualities, e2)
        (v1, v2) match {
          case (Some(Integer(v1)), Some(Integer(v2))) => op match {
            case Add  => Some(Integer(v1 + v2))
            case Sub  => Some(Integer(v1 - v2))
            case Mult => Some(Integer(v1 * v2))
            case Div  => Some(Integer(v1 / v2))
            case And | Or => None
            case Greater        => Some(Bool(v1 > v2))
            case GreaterOrEqual => Some(Bool(v1 >= v2))
            case Less           => Some(Bool(v1 < v2))
            case LessOrEqual    => Some(Bool(v1 <= v2))
            case Equal          => Some(Bool(v1 == v2))
            case NotEqual       => Some(Bool(v1 != v2)) }
          case (Some(Bool(v1)), Some(Bool(v2))) => op match {
            case Add | Sub | Mult | Div => None
            case And => Some(Bool(v1 && v2))
            case Or  => Some(Bool(v1 || v2))
            case Greater        => Some(Bool(v1 > v2))
            case GreaterOrEqual => Some(Bool(v1 >= v2))
            case Less           => Some(Bool(v1 < v2))
            case LessOrEqual    => Some(Bool(v1 <= v2))
            case Equal          => Some(Bool(v1 == v2))
            case NotEqual       => Some(Bool(v1 != v2)) }
          case _ => None }
    }
  }
}
