
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._

import com.mohiva.play.silhouette.api.Identity
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Authenticator
import com.mohiva.play.silhouette.api.Authorization

case class User(
  id: UUID,
  name: String,
  login: LoginInfo

  // email: String,
  // password: String,
  // canCreateWorlds: Boolean,
  // cookie: Option[String],
  // staySignedIn: Boolean
) extends Identity


// class UserTable(tag: Tag) extends Table[User](tag, "user") {
//   def id = column[UUID]("id", O.PrimaryKey)
//   def name = column[String]("name")
//   def login: column[LoginInfo](
//   // def email = column[String]("email")
//   // def password = column[String]("password")
//   // def canCreateWorlds = column[Boolean]("canCreateWorlds")
//   // def cookie = column[Option[String]]("cookie")
//   // def staySignedIn = column[Boolean]("staySignedIn")
//   // def * =
//   //   ((id.?, name, email, password, canCreateWorlds, cookie, staySignedIn)
//   //   <> (User.tupled, User.unapply))
// }


// object UserTable extends TableQuery(new UserTable(_)) {
//   // def insert(user: User) =
//   //   this.returning(this.map(_id)).
//   //     into((user, id) => user.copy(id = Some(id))).
//   //     +=(user)(session)
// }



// // case class IsAuthorised() extends Authorization[User, Authenticator] {
// //   override def isAuthorized[B] ...
