
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.model.read
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }
import goodnight.stories.WithStory


object Story {
  type Content = (Seq[model.Scene], Seq[model.Quality])
  case class Props(router: pages.Router, story: model.Story)
  case class State(content: Option[Content])

  class Backend(bs: BackendScope[Props, State]) {
    def loadScenes: Callback =
      bs.props.flatMap(props =>
        Request(ApiV1.Content, props.story.urlname).send.
          forStatus(200).forJson[Content].
          body.flatMap(content =>
            bs.modState(_.copy(content = Some(content))).async).
          toCallback)

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

    private def renderScene(props: Props, scene: model.Scene) =
      <.div(^.className := "scene",
        <.div(
          <.i(^.className := "fas fa-scroll" +
            (if (scene.isStart) " start" else "")),
          <.span(scene.name),
          props.router.link(pages.EditScene(props.story.urlname,
            scene.urlname))(
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
        <.p(scene.text))

    private def renderQuality(props: Props, quality: model.Quality) =
      <.div(^.className := "quality",
        <.div(
          <.i(^.className := "fas fa-hammer"),
          <.span(quality.name),
          props.router.link(pages.EditQuality(props.story.urlname,
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
        <.p(quality.description))


    def render(props: Props, state: State): VdomElement = state.content match {
      case None => Loading.component(props.router)
      case Some(content) =>
        <.div(
          <.div(^.className := "edit-canvas",
            content._1.map(renderScene(props, _)).toTagMod,
            content._2.map(renderQuality(props, _)).toTagMod),
          <.p(
            <.button(
              props.router.setOnClick(pages.AddScene(props.story.urlname)),
              <.i(^.className := "fas fa-plus-circle"),
              "New Scene")))
    }
  }


  val component = ScalaComponent.builder[Props]("edit.Story").
    initialState(State(None)).
    renderBackend[Backend].
    componentDidMount(_.backend.loadScenes).
    build


  def withStory(router: pages.Router, storyData: read.StoryState) =
    component(Props(router, ???// storyData._1
    ))

  def render(page: pages.EditStory, router: pages.Router) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, page.story, false,
        withStory(router, _))))
}
