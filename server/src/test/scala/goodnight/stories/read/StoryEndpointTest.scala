
package goodnight.stories.read

import goodnight.model
import goodnight.common.Serialise._

import goodnight.GoodnightServerTest


class StoryEndpointTest extends GoodnightServerTest {
  describe("ApiV1.Stories") {
    describe("for an un-authenticated user") {
      it("replies with 200 and Seq[model.Story]") {
        request(GET, "/api/v1/stories")({ reply =>
          assert(reply.statusCode == 200)

          val stories = read[Seq[model.Story]](reply.body)
          assume(stories.length > 0, "No stories in reply, cannot verify.")

          assert(stories.head.name.length > 0)
        })
      }
    }

    describe("for an authenticated user") {
      ignore("replies with 200 and Seq[model.Story]") {

        // assert(all stories are publicly visible)
      }
    }
  }

  describe("ApiV1.Story") {
    type Reply = (model.Story,
      Option[(model.Player, model.Activity, model.Scene)])

    describe("for a valid story") {
      describe("for an un-authenticated user") {
        it ("replies with 200 and (model.Story, None)") {
          val storyName = request(GET, "/api/v1/stories")({ reply =>
            val stories = read[Seq[model.Story]](reply.body)
            assume(stories.length > 0)
            stories.head.name
         })

          request(GET, "/api/v1/story/" + storyName)({ reply =>
            assert(reply.statusCode == 200)

            val story = read[Reply](reply.body)
            assert(story._1.name == storyName)

            // unauthenticated users have no player.
            assert(story._2 == None)
          })
        }
      }

      describe("for an authenticated user") {
        describe("without a player") {
          it ("replies with 200 and (model.Story, None)") {


          }
        }

        describe("with an existing player") {
          ignore("replies with 200 and (model.Story, Some(...))") {

          }
        }
      }
    }
  }
}
