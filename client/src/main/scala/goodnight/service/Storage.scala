
package goodnight.service

import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.scalajs.dom.ext.SessionStorage
import org.scalajs.dom.ext.LocalStorage


object Storage {


  // def get[String](key: String): Option[String] =
  //   SessionStorage(key)

  def get[T](key: String)(implicit reads: Reads[T]): Option[T] = {
    println(s"storage get: $key => ${SessionStorage(key)}")
    SessionStorage(key).flatMap(v => Json.fromJson(Json.parse(v)).asOpt)
  }

  // def set(key: String, value: String): Unit =
  //   SessionStorage.update(key, value)

  def set[T](key: String, value: T)(implicit writes: Writes[T]): Unit = {
    val asString = Json.stringify(Json.toJson(value))
    println(s"storage set: $key => $asString")
    SessionStorage.update(key, Json.stringify(Json.toJson(value)))
  }

  def remove(key: String): Unit =
    SessionStorage.remove(key)
}
