
// package goodnight.model

// import java.util.UUID

// import slick.jdbc.PostgresProfile.api._

// import com.mohiva.play.silhouette.api.LoginInfo
// // import com.mohiva.play.silhouette.api.util.PasswordInfo


// case class Profile(
//   id: UUID,
//   name: String,

//   // loginInfos: List[LoginInfo],
//   confirmed: Boolean, // has this user confirmed its identity?
//   email: Option[String],
//   // password: Option[PasswordInfo],

//   // cookie: Option[String],

//   staySignedIn: Boolean,
//   canCreateWorlds: Boolean,
// )


// class ProfileTable(tag: Tag) extends Table[Profile](tag, "profile") {
//   def id = column[UUID]("id", O.PrimaryKey)
//   def name = column[String]("name")

//   // def loginInfo = column[List[LoginInfo]]("loginInfos")
//   def confirmed = column[Boolean]("confirmed")
//   def email = column[String]("email")
//   // def password = column[PasswordInfo]("password")

//   // def cookie = column[String]("cookie")

//   def staySignedIn = column[Boolean]("staySignedIn")
//   def canCreateWorlds = column[Boolean]("canCreateWorlds")

//   def * =
//     ((id, name, // loginInfo,
//       confirmed, email.?, // password.?,
//       staySignedIn, canCreateWorlds)
//     <> (Profile.tupled, Profile.unapply))
// }


// object ProfileTable extends TableQuery(new ProfileTable(_)) {
//   // def insert(user: Profile) =
//   //   this.returning(this.map(_id)).
//   //     into((user, id) => user.copy(id = Some(id))).
//   //     +=(user)(session)
// }
