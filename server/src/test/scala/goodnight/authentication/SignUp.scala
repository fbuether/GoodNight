
package goodnight.stories.read

import goodnight.model
import goodnight.common.Serialise._

import goodnight.GoodnightServerTest


class SignUpTest extends GoodnightServerTest {
  ignore("ApiV1.SignUp") {
    describe("for an un-authenticated user") {
      describe("for POST of correct user data of new user") {
        it("replies with 201 Created and no body") {
          request(GET, "/api/v1/stories")({ reply =>
            assert(reply.statusCode == 200)

            val stories = read[Seq[model.Story]](reply.body)
            assume(stories.length > 0, "No stories in reply, cannot verify.")

            assert(stories.head.name.length > 0)
          })
        }
      }

      describe("for POST of a user with an existing user name") {
        ignore("replies with 412 Precondition Failed and an error message") {

        }
      }

      describe("for POST of a user with an existing mail address") {
        ignore("replies with 412 Precondition Failed and an error message") {
        }
      }
    }

    describe("for an authenticated user") {
      ignore("replies with 403") {

        // assert(all stories are publicly visible)
      }
    }
  }
}
