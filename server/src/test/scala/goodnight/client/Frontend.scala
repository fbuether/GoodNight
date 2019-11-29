
package goodnight.client

import controllers.AssetsFinder
import play.api.Mode
import play.api.test.FakeRequest
import play.api.test.Helpers

import goodnight.GoodnightTest


class FrontendTest extends GoodnightTest {
  describe("The static client") {
    val assetFinder = stub[AssetsFinder]
    val controller = new Frontend(Helpers.stubControllerComponents(),
      assetFinder, Mode.Prod)

    it("is a simple html page") {
      val content = Helpers.contentAsString(controller.html(FakeRequest()))
      assert(content.startsWith("<!DOCTYPE html"))
    }

    it("resolves assets with the assetsFinder") {
      (assetFinder.path _).when(*).returns("path-to-asset")

      val content = Helpers.contentAsString(controller.html(FakeRequest()))
      assert(content.contains("path-to-asset"))
    }
  }
}
