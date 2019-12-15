
package goodnight.server

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

import slick.jdbc.PostgresProfile.api._

import slick.basic.Capability
import slick.sql.FixedSqlAction
import slick.jdbc.JdbcProfile
import slick.jdbc.JdbcCapabilities
import slick.lifted.AbstractTable
import slick.lifted.TableQuery
import slick.driver.JdbcProfile
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import com.github.tminglei.slickpg._

trait PostgresProfile extends ExPostgresProfile
    with PgArraySupport
    with PgDate2Support
    with PgRangeSupport
    with PgHStoreSupport
    with PgSearchSupport
    with PgNetSupport
    with PgLTreeSupport {
  // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"
  def pgjson = "jsonb"

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support;
  // for postgres 9.5+
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API with ArrayImplicits
      with DateTimeImplicits
      with NetImplicits
      with LTreeImplicits
      with RangeImplicits
      with HStoreImplicits
      with SearchImplicits
      with SearchAssistants {
  }

  type DbConfig = slick.basic.DatabaseConfig[PostgresProfile]
  type Database = Backend#Database

  implicit class InsertTableQuery[E <: AbstractTable[_]](
    tableQuery: TableQuery[E]) {
    def insert(element: E#TableElementType):
        FixedSqlAction[Int,NoStream,Effect.Write] =
      tableQuery += element
  }
}


object PostgresProfile extends PostgresProfile
