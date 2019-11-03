
package goodnight.api.authentication

import java.util.UUID
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.mohiva.play.silhouette.api.AuthInfo
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import goodnight.server.PostgresProfile.Database
import goodnight.model.Login
import goodnight.model.LoginAuth
import goodnight.db


class CredentialAuthInfoRepository(
  database: Database)(
  implicit ec: ExecutionContext)
    extends DelegableAuthInfoDAO[PasswordInfo] {

  val classTag: scala.reflect.ClassTag[PasswordInfo] =
    scala.reflect.classTag[PasswordInfo]


  def add(loginInfo: LoginInfo, authInfo: PasswordInfo):
      Future[PasswordInfo] = {
    val auth = LoginAuth(UUID.randomUUID(),
      loginInfo.providerID, loginInfo.providerKey,
      authInfo.hasher, authInfo.password, authInfo.salt)
    database.run(db.LoginAuth().insert(auth)).map({ _ =>
      authInfo })
  }

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    val findAuthInfo = db.LoginAuth().filter(la =>
      la.providerID === loginInfo.providerID &&
        la.providerKey === loginInfo.providerKey).
      map(la => (la.hasher, la.password, la.salt)).
      take(1).result.headOption
    database.run(findAuthInfo).map(_.map({ case (h,p,s) => PasswordInfo(h,p,s) }))
  }

  def remove(loginInfo: LoginInfo): Future[Unit] = {
    val remove = db.LoginAuth().filter(la =>
      la.providerID === loginInfo.providerID &&
        la.providerKey === loginInfo.providerKey).
      delete
    database.run(remove).map(_ => ())
  }

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo):
      Future[PasswordInfo] = {
    val findAuthInfo = db.LoginAuth().filter(la =>
      la.providerID === loginInfo.providerID &&
        la.providerKey === loginInfo.providerKey).
      map(la => la.id).
      take(1).result.headOption
    database.run(findAuthInfo).flatMap({
      case Some(rowid) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    })
  }

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo):
      Future[PasswordInfo] = {
    val updateAuthInfo = db.LoginAuth().filter(la =>
      la.providerID === loginInfo.providerID &&
        la.providerKey === loginInfo.providerKey).
      take(1).
      map(la => (la.hasher, la.password, la.salt)).
      update((authInfo.hasher, authInfo.password, authInfo.salt))
    database.run(updateAuthInfo).map({ _ => authInfo })
  }
}
