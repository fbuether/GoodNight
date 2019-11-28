
package goodnight.server

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.model

import scala.reflect._


class TableQueryBase[M, T <: Table[M]](cons: Tag => T) {
  def apply(): TableQuery[T] = TableQuery[T](cons)

  type Q = Query[T, M, Seq]

  // save a new object.
  def insert(obj: M): DBIO[M] =
    apply().
      returning(apply()) += obj
}
