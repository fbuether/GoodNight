
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
  case class State(loading: Boolean)

  class Backend(bs: BackendScope[pages.Router, State]) {
    private val nameRef = Input.componentRef

    def doCreateStory =
      bs.modState(_.copy(loading = true)) >>
      Input.withValue(nameRef, name =>
        Request(ApiV1.CreateStory).withPlainBody(name).send.
          forStatus(201).forJson[model.Story].
          body.flatMap(story =>
            bs.props.flatMap(_.set(
              pages.EditStory(story.urlname))).async).toCallback)

    def render(router: pages.Router, state: State): VdomElement =
      <.div(
        <.h2("A Name for your story"),
        <.p("Every great story starts somewhere. Yours starts with a name."),
        <.div(^.className := "simple inset",
          <.h2(
            <.i(^.className := "fas fa-tag label"),
            "Name your story"),
          nameRef.component(Input.Props("Name", "name",
            List(^.autoFocus := true, ^.required := true))),
          <.button(^.tpe := "submit",
            ^.onClick --> doCreateStory,
            ^.className := (if (state.loading) "loading" else ""),
            ^.disabled := state.loading,
            <.i(^.className :=
              (if (state.loading) "far fa-spin fa-compass label"
              else "fa fa-check-square label")),
            "Create your story")))
  }

  val component = ScalaComponent.builder[pages.Router]("CreateStory").
    initialState(State(false)).
    renderBackend[Backend].
    build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "Create a new world"),
      this.component(router))
}
