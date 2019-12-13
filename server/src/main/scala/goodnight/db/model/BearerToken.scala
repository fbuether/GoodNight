
package goodnight.db.model


case class BearerToken (
  id: String,
  provider: String,
  key: String,
  lastUsed: Long,
  expiration: Long,
  timeout: Option[Long])
