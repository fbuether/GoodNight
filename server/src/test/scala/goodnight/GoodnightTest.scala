
package goodnight

import org.scalatest.FunSpec
import play.api.db.Databases
import play.api.db.evolutions._


abstract class GoodnightTest extends FunSpec {
  def withDatabase(block: Database => T): T =
    Databases.withInMemory(
      urlOptions = Map("MODE" -> "POSTGRESQL"))({ database =>
        Evolutions.withEvolutions(database)({
          block(database)
        })
      })
}
