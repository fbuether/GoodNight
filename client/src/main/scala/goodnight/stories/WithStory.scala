
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.read
import goodnight.model
import goodnight.service.Request
import goodnight.service.Conversions._
import goodnight.model.Expression
import goodnight.model.Expression.BinaryOperator


object WithStory {
  val testData: read.StoryState = (read.Story("das-schloss",
    "Das Schloss",
    "fbuether",
    "Newer Looking, But Older Rocket.png"),
    Some((read.Player("fbuether",
      "das-schloss",
      "Sir Archibald"),
      Seq(
        read.State(
          read.Quality("das-schloss",
            "fleissig",
            read.Sort.Bool,
            "Fleißig",
            "I can help you my son, I am Paddle Paul..png"),
          true),
        read.State(
          read.Quality("das-schloss",
            "fleissig TV",
            read.Sort.Integer,
            "Fleißig TV",
            "Plasma TV.png"),
          11),
        read.State(
          read.Quality("das-schloss",
            "gut-situiert",
            read.Sort.Integer,
            "Gut situiert",
            "Chea.png"),
          7)),
      read.Activity("das-schloss",
        "fbuether",
        "abwarten",
        Seq(
          read.State(read.Quality("das-schloss",
            "gut-situiert",
            read.Sort.Integer,
            "Gut situiert",
            "Chea.png"),
            7))),
      read.Scene("das-schloss",
        "abwarten",
        "# Erstmal abwarten.\n\nIrgendetwas wird schon passieren.",
        Seq(
          read.Choice("abwarten-continue",
            "Mal sehen, yes?",
            true,
            Seq(
              read.Test(
                read.Quality("das-schloss",
                  "gierig",
                  read.Sort.Integer,
                  "Gierig",
                  "Bomb.png"),
                true,
                Expression.LessOrEqual, 5),
              read.Test(
                read.Quality("das-schloss",
                  "fleissig",
                  read.Sort.Bool,
                  "Fleißig",
                  "Blue Soap.png"),
                true,
                true)
            )),
          read.Choice("abwarten-hesitate",
            "# Wirklich abwarten?\nBist Du Dir ganz sicher?",
            false,
            Seq(
              read.Test(
                read.Quality("das-schloss",
                  "unerfuellbar",
                  read.Sort.Bool,
                  "Unerfüllbar",
                  "Tree.png"),
                false,
                true)))
            )))))

  val testData2: read.StoryState = (read.Story("das-schloss",
      "Das Schloss",
      "fbuether",
      "Newer Looking, But Older Rocket.png"),
      None)



  case class Props(router: pages.Router,
    storyUrlname: String, full: Boolean, child: read.StoryState => VdomElement)
  case class State(story: Option[read.StoryState])

  class Backend(bs: BackendScope[Props, State]) {
    def setStoryOrFail(result: Try[read.StoryState]): Callback = result match {
      case Success(storyData) => bs.setState(State(Some(storyData)))
      case Failure(e) =>
        Callback.log(s"Error while fetching story: $e") >>
        bs.props.flatMap(_.router.set(pages.Stories))
    }

    def loadIfRequired(storyUrlname: String): Callback =
      Request(ApiV1.Story, storyUrlname).send.
        forStatus(200).
        forJson[read.StoryState].
        body.
        completeWith(//setStoryOrFail)
          _ => setStoryOrFail(
            Success(
              testData
            )))


    def render(props: Props, state: State): VdomElement =
      state.story match {
        case None => <.div(
          Banner.component(props.router, "Alien World.png", "Loading story…"),
          Loading.component(props.router))
        case Some(storyData) => <.div(
          Banner.component(props.router, storyData._1.image, storyData._1.name),
          props.child(storyData))
      }
  }

  // todo: re-use an already loaded story, if this component reloads with the
  // same storyUrlname.
  val component = ScalaComponent.builder[Props]("WithStory").
    initialState(State(None)).
    renderBackend[Backend].
    componentDidMount(bs => bs.backend.loadIfRequired(bs.props.storyUrlname)).
    build

}
