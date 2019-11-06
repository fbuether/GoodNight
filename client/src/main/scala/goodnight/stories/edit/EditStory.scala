
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.api.Story._
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


  case class Props(router: pages.Router, story: model.Story,
    edit: Overlay.Overlay)
  type State = Unit

  class Backend(bs: BackendScope[Props, State]) {
    def copyItem(urlname: String): Callback = Callback {
      println("copying " + urlname)
    }

    def deleteItem(urlname: String): Callback = Callback {
      println("deleteing " + urlname)
    }

  // def loadStory(router: pages.Router, name: String): AsyncCallback[VdomElement] =
  //   Request(ApiV1.Story, name).send.forJson.map({
  //     case Reply(200, Success(storyJson)) =>
  //       this.component(router, name)
  //     case e =>
  //       <.div("Error :( -> " + e)
  //   })


    def loadItems: AsyncCallback[Seq[ItemData]] =
      CallbackTo({
        List(
          ItemData(SceneItem, "at-the-docks",
            "At the docks, there is but not a sound to hear",
            """There is a certain atmosphere at the docks that seems to claw
            at your nose like a hungry sewer rat. People bustle about""",
            List("docks", "intro", "conflict")),
          ItemData(QualityItem, "cold-hard-cash",
            "Cold, hard cash", """You gotta pay for what you take. That's how
            it's always been.""", List()),
          ItemData(LocationItem, "the-docks",
            "The Docks", """The docks have been the hub of trading in this
            forsaken town. They are not, anymore, and even the residents are
            forced to take notice.""", List("docks")))
      }).async

    def onSaveScene(scene: Option[model.Scene])(rawText: String) =
      Callback.log("well, trying to save this.")



    def render(props: Props) = {
      val editingOverlay: TagMod = props.edit match {
        case Overlay.None => List().toTagMod
        case Overlay.AddScene =>
          EditScene.component(EditScene.Props(
            props.router, props.story, None, onSaveScene(None)))
        case Overlay.EditScene(sceneUrlname) =>
          Loading.suspend(props.router,
            Loader.loadScene(props.story.urlname, sceneUrlname).map({
              case Failure(error) =>
                Error.component(error, true)
              case Success(scene) =>
                EditScene.component(EditScene.Props(
                  props.router, props.story, Some(scene),
                  onSaveScene(Some(scene))))
            }))
      }

      <.div(
        <.h2("Edit Story: ", props.story.name),
        Loading.suspend(props.router, loadItems.map(items =>
          <.div(^.className := "overlay-anchor",
            <.div(^.className := "edit-canvas",
              items.map(it =>
                editItem(ItemProps(props.router, copyItem, deleteItem,
                  props.story.urlname, it))).
                toTagMod
            ),
            <.div(^.className := "add-buttons",
              <.button(
                props.router.setOnClick(pages.AddScene(props.story.urlname)),
                <.i(^.className := "fas fa-plus-circle"),
                "New Scene"),
              <.button(
                <.i(^.className := "fas fa-plus-circle"),
                "New Quality"),
              <.button(
                <.i(^.className := "fas fa-plus-circle"),
                "New Location")),
            editingOverlay)
        )))
    }
  }

  val component = ScalaComponent.builder[Props]("EditStory").
    initialState[State](()).
    renderBackend[Backend].
    build


  def render(page: pages.EditStory, router: pages.Router) =
    Shell.component(router)(
      Banner.component((router, "Alien World.png", "World:"+page.story)),

      Loading.suspend(router,
        Loader.loadStory(page.story).map({
          case Failure(error) =>
            Error.component(error, false)
          case Success(story) =>
            component(Props(router, story, Overlay.None))
        })))

  // def addScene(page: pages.AddScene, router: pages.Router) =
  //   Shell.component(router)(
  //     Banner.component((router, "Alien World.png", "World:"+page.story+"/add")),
  //     component((router, page.story, Overlay.AddScene)))

  def editScene(page: pages.EditScene, router: pages.Router) =
    Shell.component(router)(
      Banner.component((router, "Alien World.png",
        "World:" + page.story + "/" + page.scene)),

      Loading.suspend(router,
        Loader.loadStory(page.story).map({
          case Failure(error) =>
            Error.component(error, false)
          case Success(story) =>
            component(Props(router, story, Overlay.EditScene(page.scene)))
        })))

  // def editQuality(page: pages.EditQuality, router: pages.Router) =
  //   Shell.component(router)(
  //     Banner.component((router, "Alien World.png",
  //       "World:" + page.story + "/~/" + page.quality)),
  //     component((router, page.story, Overlay.EditQuality(page.quality))))
}
