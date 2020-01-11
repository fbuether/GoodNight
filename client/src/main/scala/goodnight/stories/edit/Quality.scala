
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


object Quality {
  case class Props(router: pages.Router, story: model.Story,
    qualityUrlname: Option[String])
  case class State(quality: Option[model.Quality], changed: Boolean,
    saving: Boolean)

  class Backend(bs: BackendScope[Props, State]) {
    def loadQuality: Callback =
      bs.props.flatMap(props => props.qualityUrlname match {
          case None => Callback.empty
          case Some(qualityUrlname) =>
            Request(ApiV1.Quality, props.story.urlname, qualityUrlname).send.
              forStatus(200).forJson[model.Quality].
              body.flatMap(quality =>
                bs.modState(_.copy(quality = Some(quality))).async).
              toCallback
        })

    def cancel: Callback =
      bs.props.flatMap(props =>
        // todo: ask for confirmation if (state.changed)
        props.router.set(pages.EditStory(props.story.urlname)))

    def save: Callback =
      bs.modState(_.copy(saving = true)) >>
      bs.props.zip(bs.state).flatMap({ case (props, state) =>
        editorRef.get.flatMapCB(_.backend.get).flatMap({ rawText =>
          val request = props.qualityUrlname match {
            case None => Request(ApiV1.CreateQuality, props.story.urlname)
            case Some(quality) => Request(ApiV1.SaveQuality, props.story.urlname,
              quality)
          }

          (request.withPlainBody(rawText).send.forStatus(202) >>
            bs.modState(_.copy(saving = false, changed = false)).async).
            toCallback
      })
    })


    def setChanged =
      bs.modState(_.copy(changed = true)).void

    val editorRef = Editor.componentRef

    val newQuality = ("Write a new Quality", "# New Quality")
    def ofQuality(quality: model.Quality) = ("Edit: " + quality.name, quality.description)

    def renderEditor(props: Props, state: State, text: (String, String)) = {
      val (title, content) = text
      val canSave = !state.saving && state.changed

      <.div(
        <.h3(title),
        editorRef.component(Editor.Props(content, setChanged)),
        <.div(^.className := "as-columns for-buttons",
          <.div(
            <.button(
              ^.onClick --> cancel,
              <.i(^.className := "fas fa-ban label"),
              "Discard")),
          <.div(
            <.button(
              ^.onClick --> save,
              ^.className :=
                (if (state.saving) " loading" else "") +
                (if (!canSave) " locked" else ""),
              (^.disabled := true).when(!canSave),
              <.i(^.className :=
                (if (state.saving) "far fa-spin fa-compass label"
                else "fa fa-check-square label")),
              (if (state.saving) "Saving…"
              else "Save")))))
    }

    def render(props: Props, state: State): VdomElement =
      (props.qualityUrlname, state.quality) match {
        case (None, None) => renderEditor(props, state, newQuality)
        case (Some(_), Some(quality)) =>
          renderEditor(props, state, ofQuality(quality))
        case _ => Loading.component(props.router)
      }
  }


  val component = ScalaComponent.builder[Props]("edit.Quality").
    initialStateFromProps(props =>
      // if we create a new quality, we can save immediately.
      (State(None, props.qualityUrlname.isEmpty, false))).
    renderBackend[Backend].
    componentDidMount(_.backend.loadQuality).
    build


  def render(router: pages.Router, story: String, quality: Option[String]) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, story, false, storyData =>
        component(Props(router, storyData._1, quality)))))

  def renderEdit(page: pages.EditQuality, router: pages.Router) =
    render(router, page.story, Some(page.quality))

  def renderAdd(page: pages.AddQuality, router: pages.Router) =
    render(router, page.story, None)
}
