
package goodnight.client

import play.api.Mode
import play.api.test.FakeRequest
import play.api.test.Helpers

import goodnight.GoodnightTest


class FrontendTest extends GoodnightTest {
  describe("The static client") {
    val controller = new Frontend(Helpers.stubControllerComponents(),
      null, Mode.Prod
    )

    it("is a simple html page") {
      val result = controller.html(FakeRequest())
      val content = Helpers.contentAsString(result)
      assert(content.startsWith("<html"))
    }
  }
}
