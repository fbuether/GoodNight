
package goodnight.stories.edit

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
import goodnight.stories.WithStory


object CreateStory {
  case class State(loading: Boolean,
    content: Option[(model.edit.Story, model.edit.Content)])

  class Backend(bs: BackendScope[pages.Router, State]) {
    private val nameRef = Input.componentRef

    def doCreateStory =
      bs.modState(_.copy(loading = true)) >>
      Input.withValue(nameRef, name =>
        Request(ApiV1.CreateStory).withPlainBody(name).send.
          forStatus(201).forJson[(model.edit.Story, model.edit.Content)].
          body.flatMap(storyContent =>
            bs.props.flatMap(_.set(pages.EditStory(storyContent._1.urlname))).
              async).toCallback)

    def render(router: pages.Router, state: State): VdomElement =
      <.div(
        <.h2("A Name for your story"),
        <.p("Every great story starts somewhere.",
          "Yours starts with a name."),
        <.form(^.className := "simple centered half inset",
          ^.onSubmit ==> (_.preventDefaultCB >> doCreateStory),
          <.h2(
            <.i(^.className := "fas fa-tag label"),
            "Name your story"),
          nameRef.component(Input.Props("Name", "name",
            List(^.autoFocus := true, ^.required := true))),
          SavingButton.render("small atRight", "far fa-check-square",
            true, state.loading)("Create your story!")))
  }

  val component = ScalaComponent.builder[pages.Router]("CreateStory").
    initialState(State(false, None)).
    renderBackend[Backend].
    build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "Create a new world"),
      this.component(router))
}
