
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import java.util.UUID
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Conversions._
import goodnight.service.Loader
import goodnight.service.{ Request, Reply }


object EditStory {
  // Items that show on the edit canvas.
  trait ItemType
  object SceneItem extends ItemType
  object QualityItem extends ItemType
  object LocationItem extends ItemType

  case class ItemData(ty: ItemType, urlname: String,
    title: String, text: String, tags: List[String])

  case class ItemProps(router: pages.Router,
    onCopy: String => Callback,
    onDelete: String => Callback,
    story: String, item: ItemData)

  val editItem = ScalaComponent.builder[ItemProps]("EditItem").
    render_P({ props: ItemProps =>
      val itemClass = props.item.ty match {
        case SceneItem => "scene"
        case QualityItem => "quality"
        case LocationItem => "location"
      }

      val icon = props.item.ty match {
        case SceneItem => "fas fa-scroll"
        case QualityItem => "fas fa-hammer"
        case LocationItem => "fas fa-map-marked"
      }

      <.div(^.className := itemClass,
        <.div(
          <.i(^.className := icon),
          <.span(props.item.title),
          props.router.link(pages.EditScene(props.story, props.item.urlname))(
            ^.title := "Edit this scene",
            <.i(^.className := "fas fa-pen-fancy")),
          <.a(^.className := "clickable",
            ^.onClick --> props.onCopy(props.item.urlname),
            ^.title := "Copy this scene",
            <.i(^.className := "far fa-copy")),
          <.a(^.className := "clickable danger",
            ^.onClick --> props.onDelete(props.item.urlname),
            ^.title := "Delete this scene",
            <.i(^.className := "far fa-trash-alt"))),
        <.p(props.item.text),
        <.div(^.className := "tags",
          props.item.tags.map(t => <.span(t)).toTagMod))
    }).
    build


  // the edit canvas itself.

  object Overlay {
    trait Overlay
    case object None extends Overlay
    case object AddScene extends Overlay
    case class EditScene(scene: String) extends Overlay
    case class EditQuality(quality: String) extends Overlay
  }


  case class Props(router: pages.Router, story: String,
    edit: Overlay.Overlay)

  case class State(
    story: Option[model.Story],
    scenes: Option[List[model.Scene]],
    loading: Boolean,
    overlay: Overlay.Overlay)

  class Backend(bs: BackendScope[Props, State]) {
    def copyItem(urlname: String): Callback = Callback {
      println("copying " + urlname)
    }

    def deleteItem(urlname: String): Callback = Callback {
      println("deleteing " + urlname)
    }


    def loadState(storyUrlname: String): AsyncCallback[Unit] = {
      Loader.loadStory(storyUrlname).flatMap({
        case Failure(error) =>
          AsyncCallback.pure(println("well, that was a pitty: " + error))
        case Success(story) =>
          bs.modState(_.copy(story = Some(story))).async
      }).flatMap(_ =>
        Loader.loadScenes(storyUrlname).flatMap({
          case Failure(error) =>
            AsyncCallback.pure(println("well, another pitty; " + error))
          case Success(scenes) =>
            bs.modState(_.copy(scenes = Some(scenes))).async
        }))
    }


    def loadStoryOnUpdate(state: State, nextProps: Props,
      modState: ((State => State) => CallbackTo[Unit])): CallbackTo[Unit] = {
      val refresh = state.story.
        map(_.urlname != nextProps.story).
        getOrElse(true)

      if (refresh) loadState(nextProps.story).toCallback
      else modState(_.copy(story = state.story, scenes = state.scenes))
    }

    // CallbackTo({
    //   List(
    //     ItemData(SceneItem, "at-the-docks",
    //       "At the docks, there is but not a sound to hear",
    //       """There is a certain atmosphere at the docks that seems to claw
    //       at your nose like a hungry sewer rat. People bustle about""",
    //       List("docks", "intro", "conflict")),
    //     ItemData(QualityItem, "cold-hard-cash",
    //       "Cold, hard cash", """You gotta pay for what you take. That's how
    //       it's always been.""", List()),
    //     ItemData(LocationItem, "the-docks",
    //       "The Docks", """The docks have been the hub of trading in this
    //       forsaken town. They are not, anymore, and even the residents are
    //       forced to take notice.""", List("docks")))
    // }).async


    def onSaveScene(storyUrlname: String, scene: Option[model.Scene])(
      rawText: String) = scene match {
      case Some(scene) =>
        Request(ApiV1.EditScene, storyUrlname, scene.urlname).
          withBody(Json.obj(
            "text" -> rawText)).send.
          map({
            case Reply(200, _) => println("okay, updated!")
            case e => println("error while saving: " + e)
          })
      case None =>
        Request(ApiV1.CreateScene, storyUrlname).
          withBody(Json.obj(
            "text" -> rawText)).send.
          map({
            case Reply(201, _) => println("okay, created!")
            case e => println("error while saving: " + e)
          })
    }

    def renderOverlay(props: Props, state: State): TagMod =
      (state.story, props.edit) match {
        case (Some(story), Overlay.AddScene) =>
          EditScene.component(EditScene.Props(
            props.router, story, None, onSaveScene(story.urlname, None)))
        case (Some(story), Overlay.EditScene(sceneUrlname)) =>
          state.scenes.flatMap(_.find(_.urlname == sceneUrlname)) match {
            case Some(scene) =>
              EditScene.component(EditScene.Props(
                props.router, story, Some(scene),
                onSaveScene(story.urlname, Some(scene))))
            case None =>
              TagMod.empty
          }
        case _ => TagMod.empty
      }

    def render(props: Props, state: State) = {
      <.div(^.className := "overlay-anchor",
        Banner.component(props.router, "Alien World.png", "World:"+props.story),
        <.div(^.className := "add-buttons",
          <.button(
            props.router.setOnClick(pages.AddScene(props.story)),
            <.i(^.className := "fas fa-plus-circle"),
            "New Scene"),
          <.button(
            props.router.setOnClick(pages.AddQuality(props.story)),
            <.i(^.className := "fas fa-plus-circle"),
            "New Quality"),
          <.button(
            props.router.setOnClick(pages.AddLocation(props.story)),
            <.i(^.className := "fas fa-plus-circle"),
            "New Location")),
        <.div(^.className := "edit-canvas",
          state.scenes.getOrElse(List()).map(scene =>
            editItem(ItemProps(props.router, copyItem, deleteItem,
              props.story,
              ItemData(SceneItem, scene.urlname,
                scene.title, scene.text, List())))).
            toTagMod),
        renderOverlay(props, state))
    }
  }

  val component = ScalaComponent.builder[Props]("EditStory").
    initialState(State(None, None, false, Overlay.None)).
    renderBackend[Backend].
    // (re-)load story/scenes on navigating here.
    componentDidMount(component =>
      component.backend.loadState(component.props.story).toCallback).
    componentWillReceiveProps(update => update.backend.loadStoryOnUpdate(
      update.state, update.nextProps, update.modState)).
    build

  def showWith(router: pages.Router, story: String, overlay: Overlay.Overlay) =
    Shell.component(router)(
      component(Props(router, story, overlay)))


  def render(page: pages.EditStory, router: pages.Router) =
    showWith(router, page.story, Overlay.None)

  def addScene(page: pages.AddScene, router: pages.Router) =
    showWith(router, page.story, Overlay.AddScene)

  def editScene(page: pages.EditScene, router: pages.Router) =
    showWith(router, page.story, Overlay.EditScene(page.scene))

  // def editQuality(page: pages.EditQuality, router: pages.Router) =
  //   showWith(router, page.story, Overlay.EditQuality(page.quality))
}
