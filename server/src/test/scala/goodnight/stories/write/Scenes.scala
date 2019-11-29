
package goodnight.stories.write

import play.api.test.Helpers._
import com.mohiva.play.silhouette.test._

import goodnight.GoodnightTest


import goodnight.api.authentication.AuthEnvironment


object ScenesTest extends GoodnightTest {
  // val components = Helpers.stubControllerComponents()

  // val identity = Id(LoginInfo("facebook", "apollonia.vanova@watchmen.com"))
  // implicit val env = FakeEnvironment[AuthEnvironment](
  //   Seq(identity.loginInfo -> identity))

  // val request = FakeRequest().withAuthenticator(identity.loginInfo)


  // describe("the urlname of a story") {
  //   withDatabase { database =>
  //     val storyController = new Stories(
  //       components, database, auth)

  //     it("must contain no spaces") {
  //       val urlname = storyController.urlnameOf("this is a funny story!")
  //       assert(!urlname.contains(" "))
  //     }
  //   }
  // }
}
