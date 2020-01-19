
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.edit
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }
import goodnight.stories.WithStory


object Quality {
  case class Props(router: pages.Router, story: edit.Story,
    qualityUrlname: Option[String])
  case class State(quality: Option[edit.Quality], changed: Boolean,
    saving: Boolean, loading: Boolean)

  class Backend(bs: BackendScope[Props, State]) {
    def loadQuality: Callback =
      bs.props.flatMap(props => props.qualityUrlname match {
          case None => bs.modState(_.copy(loading = false))
          case Some(qualityUrlname) =>
            Request(ApiV1.Quality, props.story.urlname, qualityUrlname).send.
              forStatus(200).forJson[edit.Quality].
              body.flatMap(quality =>
                bs.modState(_.copy(
                  quality = Some(quality),
                  loading = false)).async).toCallback
        })

    def cancel: Callback =
      bs.props.flatMap(props =>
        // todo: ask for confirmation if (state.changed)
        props.router.set(pages.EditStory(props.story.urlname)))

    def save: Callback =
      // todo: check for intermediate changes, e.g. with a revision number
      bs.modState(_.copy(saving = true)) >>
      bs.props.zip(bs.state).flatMap({ case (props, state) =>
        editorRef.get.flatMapCB(_.backend.get).flatMap({ rawText =>
          val request = state.quality match {
            case None => Request(ApiV1.CreateQuality, props.story.urlname)
            case Some(quality) => Request(ApiV1.SaveQuality, props.story.urlname,
              quality.urlname)
          }

          (request.withPlainBody(rawText).send.
            forStatus(202).forJson[edit.Quality].
            body.flatMap(quality =>
              bs.modState(_.copy(
                quality = Some(quality),
                saving = false,
                changed = false)).async).toCallback)
      })
    })

    val editorRef = Editor.componentRef
    def setChanged = bs.modState(_.copy(changed = true)).void


    def renderEditor(props: Props, state: State, quality: Option[edit.Quality]) = {
      val title = quality.map(_.name).getOrElse("A new Quality")
      val content = quality.map(_.raw).getOrElse("$ name: new quality")
      val canSave = !state.saving && state.changed

      <.div(
        <.h3("Writing: ", title),
        editorRef.component(Editor.Props(content, setChanged)),
        <.div(^.className := "as-columns for-buttons",
          <.div(
            <.button(
              ^.onClick --> cancel,
              <.i(^.className := "label " +
                (if (state.changed) "far fa-trash-alt"
                else "far fa-arrow-alt-circle-left")),
              (if (state.changed) "Discard" else "Return"))),
          <.div(
            SavingButton.render(canSave, state.saving, save)(
              if (state.saving) "Saving…" else "Save"))))
    }

    def render(props: Props, state: State): VdomElement =
      if (state.loading) Loading.component(props.router)
      else renderEditor(props, state, state.quality)
  }


  val component = ScalaComponent.builder[Props]("edit.Quality").
    initialStateFromProps(props =>
      // if we create a new quality, we can save immediately.
      (State(None, props.qualityUrlname.isEmpty, false, true))).
    renderBackend[Backend].
    componentDidMount(_.backend.loadQuality).
    build


  def render(router: pages.Router, story: String, quality: Option[String]) =
    Shell.component(router)(
      WithEditStory.component(WithEditStory.Props(router, story,
        content => component(Props(router, content.story, quality)))))

  def renderEdit(page: pages.EditQuality, router: pages.Router) =
    render(router, page.story, Some(page.quality))

  def renderAdd(page: pages.AddQuality, router: pages.Router) =
    render(router, page.story, None)
}
