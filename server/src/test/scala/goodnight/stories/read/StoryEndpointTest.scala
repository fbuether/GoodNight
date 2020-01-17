
package goodnight.stories.read

import goodnight.model
import goodnight.common.Serialise._

import goodnight.GoodnightServerTest


class StoryEndpointTest extends GoodnightServerTest {
  describe("ApiV1.Stories") {
    describe("for an un-authenticated user") {
      it("replies with 200 and Seq[model.read.Story]") {
        request(GET, "/api/v1/stories")({ reply =>
          assert(reply.statusCode == 200)

          val stories = read[Seq[model.read.Story]](reply.body)
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
    describe("for a valid story") {
      describe("for an un-authenticated user") {
        it ("replies with 200 and (model.read.Story, None)") {
          val storyUrlname = request(GET, "/api/v1/stories")({ reply =>
            val stories = read[Seq[model.read.Story]](reply.body)
            assume(stories.length > 0)
            stories.head.urlname
         })

          request(GET, "/api/v1/story/" + storyUrlname)({ reply =>
            assert(reply.statusCode == 200)

            val story = read[model.read.StoryState](reply.body)
            assert(story._1.urlname == storyUrlname)

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
