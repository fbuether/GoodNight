
package goodnight.db.model

import java.util.UUID


// stores verification information for a specific login.
case class LoginAuth(
  id: UUID,
  providerID: String,
  providerKey: String,
  hasher: String,
  password: String,
  salt: Option[String])
    extends DbModel
