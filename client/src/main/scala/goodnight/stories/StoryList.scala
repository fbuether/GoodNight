
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.read
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


object StoryList {
  case class Props(router: pages.Router,
    query: String = "",
    limit: Option[Int] = None,
    storyPage: String => pages.Page = pages.Story(_))
  case class State(stories: Option[Seq[read.Story]])

  class Backend(bs: BackendScope[Props, State]) {
    def load: Callback =
      bs.props.flatMap(props =>
        Request(ApiV1.Stories).query(props.query).send.
          forStatus(200).forJson[List[read.Story]].
          body.completeWith({
            case Success(stories) => bs.setState(State(Some(stories)))
            case Failure(_) => bs.setState(State(Some(Seq()))) }))

    val renderNoStories =
      <.p("No stories found. Sorry about that.")

    def renderStory(props: Props, story: read.Story) =
      <.li(
        props.router.link(props.storyPage(story.urlname))(
          Image.component(props.router, story.image),
          <.div(story.name)))

    def renderStories(props: Props, stories: Seq[read.Story]) =
      <.ul(^.className := "story-list as-tiles links",
        stories.map(renderStory(props, _)).toTagMod)

    def render(props: Props, state: State): VdomElement = state.stories match {
      case None => Loading.component(props.router)
      case Some(Seq()) => renderNoStories
      case Some(stories) => renderStories(props,
        stories.take(props.limit.getOrElse(stories.length))) }
  }

  val component = ScalaComponent.builder[Props]("StoryList").
    initialState(State(None)).
    renderBackend[Backend].
    componentDidMount(_.backend.load).
    build
}
