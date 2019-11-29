
package goodnight

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSpec
import play.api.test.Helpers

// import play.api.db.Databases
// import play.api.db.evolutions._



abstract class GoodnightTest extends FunSpec with MockFactory {
  implicit val timeout = Helpers.defaultAwaitTimeout

  // def withDatabase(block: Database => T): T =
  //   Databases.withInMemory(
  //     urlOptions = Map("MODE" -> "POSTGRESQL"))({ database =>
  //       Evolutions.withEvolutions(database)({
  //         block(database)
  //       })
  //     })
}
