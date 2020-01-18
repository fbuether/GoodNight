
package goodnight.server

import java.util.UUID

import goodnight.db.model
import goodnight.server.PostgresProfile.api._


abstract class TableBase[M <: model.DbModel](tag: Tag, name: String)
    extends Table[M](tag, name) {
  def id: Rep[UUID]
}
