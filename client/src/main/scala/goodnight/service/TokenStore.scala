
package goodnight.service

import scala.collection.mutable.Buffer

import org.scalajs.dom.ext.LocalStorage


trait TokenStore {
  def store(token: String): Unit
  def onChange(handler: Option[String] => Unit): Unit
  def get: Option[String]
  def clear: Unit
}


object TokenStore
    extends TokenStore {
  private val tokenKey = "auth-token"

  private var changeListener: Buffer[Option[String] => Unit] = Buffer.empty

  def store(token: String): Unit = {
    // only update token if it actually is different from before.
    if (get != Some(token) || get == None) {
      LocalStorage.update(tokenKey, token)
      changeListener.foreach(listener => listener(Some(token)))
    }
  }

  def onChange(handler: Option[String] => Unit): Unit = {
    changeListener += handler
  }

  def get: Option[String] =
    LocalStorage(tokenKey)

  def clear: Unit = {
    LocalStorage.remove(tokenKey)
    changeListener.foreach(listener => listener(None))
  }
}
