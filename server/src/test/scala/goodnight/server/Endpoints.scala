
package goodnight.server

import goodnight.GoodnightServerTest


class EndpointsTest extends GoodnightServerTest {
  describe("ApiV1.Frontend()") {
    it("replies with 200 and a html document") {
      request(GET, "/")({ reply =>
        assert(reply.statusCode == 200)
        assert(reply.headers("Content-Type").startsWith("text/html"))
        assert(reply.body.startsWith("<!DOCTYPE html"))
      })
    }
  }

  describe("ApiV1.Asset(file)") {
    it("provides images") {
      request(GET, "/assets/images/buuf/Cloudy Night.png")({ reply =>
        assert(reply.statusCode == 200)
        assert(reply.headers("Content-Type").startsWith("image/"))
        assert(reply.body.length >= 1)
      })
    }
  }
}
