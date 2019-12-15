
package goodnight

import play.api.LoggerConfigurator
import controllers.AssetsFinder
import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method
import fr.hmil.roshttp.Protocol
import fr.hmil.roshttp.body.PlainTextBody
import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.HttpResponse
import fr.hmil.roshttp.response.SimpleHttpResponse
import monix.execution.Scheduler.Implicits.global
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSpec
import org.scalatestplus.play._
import org.scalatestplus.play.components._
import play.api.Mode
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try
import java.net.URI

// import play.api.db.Databases
// import play.api.db.evolutions._

import goodnight.server.GoodnightNoHttpFiltersComponents



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


abstract class GoodnightServerTest extends GoodnightTest
    with OneServerPerTestWithComponents {
  override def components =
    new GoodnightNoHttpFiltersComponents(context)

  protected def request(method: Method, url: String)(
    cont: SimpleHttpResponse => Unit): Unit = {
    // move the path through java.net.URI, in order to encode any url bits
    // (for example, spaces).
    val uri = new URI("http", null, "localhost", port, url, null, null)
    val request = HttpRequest().
      withProtocol(Protocol.HTTP).
      withMethod(method).
      withHost("localhost").
      withPort(port).
      withPath(uri.getRawPath)

    withClue(s"Request: ${request.method.toString} ${request.url}\n")({
      sendRequest(request)(cont)
    })
  }

  protected def sendRequest(request: HttpRequest)(
    cont: SimpleHttpResponse => Unit): Unit = {
    val replyFuture = request.send.
      recover({ case (e: HttpException[_]) => e.response match {
        case r: SimpleHttpResponse => r }})
    Await.ready(replyFuture, Duration.Inf)
    val replyOpt = replyFuture.value

    assert(replyOpt.isDefined, "Request did not yield a result.")
    assert(replyOpt.get.isSuccess, s"Exception: ${replyOpt.get}")
    val reply = replyOpt.get.get
    withClue(s"""Reply: ${reply.statusCode}\n${reply.headers}\n
      | ${reply.body}\n""".stripMargin)({
      cont(reply)
    })
  }
}
