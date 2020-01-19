
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.model.edit
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


object Story {
  case class Props(router: pages.Router, content: edit.Content)
  case class State(content: edit.Content)

  class Backend(bs: BackendScope[Props, State]) {
    // def loadScenes: Callback =
    //   bs.props.flatMap(props =>
    //     Request(ApiV1.Content, props.story.urlname).send.
    //       forStatus(200).forJson[Content].
    //       body.flatMap(content =>
    //         bs.modState(_.copy(content = Some(content))).async).
    //       toCallback)

    def copyScene(urlname: String): Callback = Callback {
      println("copying " + urlname)
    }

    def deleteScene(urlname: String): Callback = Callback {
      println("deleteing " + urlname)
    }

    def copyQuality(urlname: String): Callback = Callback {
      println("copying " + urlname)
    }

    def deleteQuality(urlname: String): Callback = Callback {
      println("deleteing " + urlname)
    }

    private def renderScene(router: pages.Router, story: edit.Story,
      scene: edit.SceneHeader) =
      <.div(^.className := "scene",
        <.div(
          <.i(^.className := "fas fa-scroll" +
            (if (scene.start) " start" else "")),
          <.span(scene.name),
          router.link(pages.EditScene(story.urlname, scene.urlname))(
            ^.title := "Edit this scene",
              <.i(^.className := "fas fa-pen-fancy")),
          <.a(^.className := "clickable",
            ^.onClick --> copyScene(scene.urlname),
            ^.title := "Copy this scene",
            <.i(^.className := "far fa-copy")),
          <.a(^.className := "clickable danger",
            ^.onClick --> deleteScene(scene.urlname),
            ^.title := "Delete this scene",
            <.i(^.className := "far fa-trash-alt"))),
        <.p(scene.textHead))

    private def renderQuality(router: pages.Router, story: edit.Story,
      quality: edit.QualityHeader) =
      <.div(^.className := "quality",
        <.div(
          <.i(^.className := "fas fa-hammer"),
          <.span(quality.name),
          router.link(pages.EditQuality(story.urlname,
            quality.urlname))(
            ^.title := "Edit this quality",
              <.i(^.className := "fas fa-pen-fancy")),
          <.a(^.className := "clickable",
            ^.onClick --> copyQuality(quality.urlname),
            ^.title := "Copy this quality",
            <.i(^.className := "far fa-copy")),
          <.a(^.className := "clickable danger",
            ^.onClick --> deleteQuality(quality.urlname),
            ^.title := "Delete this quality",
            <.i(^.className := "far fa-trash-alt"))),
        <.p(quality.textHead))


    def render(props: Props, state: State): VdomElement =
      <.div(
        <.div(^.className := "edit-canvas",
          state.content.scenes.map(renderScene(props.router,
            state.content.story, _)).toTagMod,
          state.content.qualities.map(renderQuality(props.router,
            state.content.story, _)).toTagMod),
        <.p(
          <.button(
            props.router.setOnClick(
              pages.AddScene(state.content.story.urlname)),
            <.i(^.className := "fas fa-plus-circle"),
            "New Scene"),
          <.button(
            props.router.setOnClick(
              pages.AddQuality(state.content.story.urlname)),
            <.i(^.className := "fas fa-plus-circle"),
            "New Quality")))
  }


  val component = ScalaComponent.builder[Props]("edit.Story").
    initialStateFromProps(props => State(props.content)).
    renderBackend[Backend].
    build


  def render(page: pages.EditStory, router: pages.Router) =
    Shell.component(router)(
      WithEditStory.component(WithEditStory.Props(router, page.story,
        content => component(Props(router, content)))))
}
