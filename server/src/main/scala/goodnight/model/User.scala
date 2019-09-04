
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._


case class User(
  id: Option[UUID],
  name: String,
  email: String,
  password: String,
  canCreateWorlds: Boolean,
  cookie: Option[String],
  staySignedIn: Boolean
)


class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def email = column[String]("email")
  def password = column[String]("password")
  def canCreateWorlds = column[Boolean]("canCreateWorlds")
  def cookie = column[Option[String]]("cookie")
  def staySignedIn = column[Boolean]("staySignedIn")
  def * =
    ((id.?, name, email, password, canCreateWorlds, cookie, staySignedIn)
    <> (User.tupled, User.unapply))
}


object UserTable extends TableQuery(new UserTable(_)) {
  // def insert(user: User) =
  //   this.returning(this.map(_id)).
  //     into((user, id) => user.copy(id = Some(id))).
  //     +=(user)(session)
}
