
package goodnight.db.model


// stores association from way of login to account.
case class Login(
  id: UUID,
  user: UUID,
  providerID: String,
  providerKey: String
)
