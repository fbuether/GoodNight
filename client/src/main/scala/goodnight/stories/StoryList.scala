
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


object StoryList {
  case class Props(router: pages.Router, limit: Option[Int] = None,
    publicOnly: Boolean = true)
  case class State(stories: Option[Seq[model.Story]])

  class Backend(bs: BackendScope[Props, State]) {
    def load: Callback =
      bs.props.flatMap(props =>
        Request(ApiV1.Stories).
          query(if (props.publicOnly) "publicOnly" else "").
          send.
          forStatus(200).forJson[List[model.Story]].
          body.completeWith({
            case Success(stories) => bs.setState(State(Some(stories)))
            case Failure(_) => bs.setState(State(Some(Seq()))) }))

    val renderNoStories =
      <.p("No stories found. Sorry about that.")

    def renderStory(router: pages.Router, story: model.Story) =
      <.li(
        router.link(pages.Story(story.urlname))(
          Image.component(router, story.image),
          <.div(story.name)))

    def renderStories(router: pages.Router, stories: Seq[model.Story]) =
      <.ul(^.className := "story-list as-tiles links",
        stories.map(renderStory(router, _)).toTagMod)

    def render(props: Props, state: State): VdomElement = state.stories match {
      case None => Loading.component(props.router)
      case Some(Seq()) => renderNoStories
      case Some(stories) => renderStories(props.router,
        stories.take(props.limit.getOrElse(stories.length))) }
  }

  val component = ScalaComponent.builder[Props]("StoryList").
    initialState(State(None)).
    renderBackend[Backend].
    componentDidMount(_.backend.load).
    build
}
