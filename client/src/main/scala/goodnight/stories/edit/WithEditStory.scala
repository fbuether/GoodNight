
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.edit
import goodnight.service.Request
import goodnight.service.Conversions._
import goodnight.model.Expression
import goodnight.model.Expression.BinaryOperator


object WithEditStory {
  case class Props(router: pages.Router, storyUrlname: String,
    child: edit.Content => VdomElement)
  case class State(content: Option[edit.Content])

  class Backend(bs: BackendScope[Props, State]) {
    def setStoryOrFail(result: Try[edit.Content]): Callback = result match {
      case Success(content) => bs.setState(State(Some(content)))
      case Failure(e) =>
        Callback.log(s"Error while fetching story: $e") >>
        bs.props.flatMap(_.router.set(pages.Stories))
    }

    def loadIfRequired(storyUrlname: String): Callback =
      Request(ApiV1.Content, storyUrlname).send.
        forStatus(200).forJson[edit.Content].
        body.completeWith(setStoryOrFail)

    def render(props: Props, state: State): VdomElement =
      state.content match {
        case None => <.div(
          Banner.component(props.router, "Alien World.png", "Loading story…"),
          Loading.component(props.router))
        case Some(content) => <.div(
          Banner.component(props.router, content.story.image,
            content.story.name),
          props.child(content))
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
