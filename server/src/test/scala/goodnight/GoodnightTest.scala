
package goodnight

import play.api.test.Helpers

import org.scalatest.FunSpec
// import play.api.db.Databases
// import play.api.db.evolutions._



abstract class GoodnightTest extends FunSpec {
  implicit val timeout = Helpers.defaultAwaitTimeout

  // def withDatabase(block: Database => T): T =
  //   Databases.withInMemory(
  //     urlOptions = Map("MODE" -> "POSTGRESQL"))({ database =>
  //       Evolutions.withEvolutions(database)({
  //         block(database)
  //       })
  //     })
}
