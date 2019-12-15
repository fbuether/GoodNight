
package goodnight.server

import controllers.AssetsFinder
import org.scalatestplus.play._
import org.scalatestplus.play.components._
import monix.execution.Scheduler.Implicits.global
import play.api.Mode
import play.api.test.FakeRequest
import play.api.test.Helpers
import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method
import fr.hmil.roshttp.body.PlainTextBody
import fr.hmil.roshttp.response.SimpleHttpResponse
import fr.hmil.roshttp.response.HttpResponse
import fr.hmil.roshttp.exceptions.HttpException
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.Future

import goodnight.GoodnightServerTest


class EndpointsTest extends GoodnightServerTest {
  describe("ApiV1.Frontend()") {
    it("replies with 200 and a html document") {
      request(Method.GET, "/")({ reply =>
        assert(reply.statusCode == 200)
        assert(reply.headers("Content-Type").startsWith("text/html"))
        assert(reply.body.startsWith("<!DOCTYPE html"))
      })
    }
  }

  describe("ApiV1.Asset(file)") {
    it("provides images") {
      request(Method.GET, "/assets/images/buuf/Cloudy Night.png")({ reply =>
        assert(reply.statusCode == 200)
        assert(reply.headers("Content-Type").startsWith("image/"))
        assert(reply.body.length >= 1)
      })
    }
  }
}
