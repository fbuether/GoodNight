
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.util.{ Try, Success, Failure }
import play.api.libs.json._
import play.api.libs.functional.syntax._

import goodnight.client.pages
import goodnight.service.{ Request, Reply }
import goodnight.service.Conversions._
import goodnight.components.Input
import goodnight.components.Shell
import goodnight.components.Banner
import goodnight.components.Loading

import goodnight.common.ApiV1
import goodnight.model
import goodnight.common.api.Story._


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


  type Props = (pages.Router, String, Overlay.Overlay)
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

    def render(p: Props) = {
      val (router, story, edit) = p

      val editingOverlay: TagMod = edit match {
        case Overlay.None => List().toTagMod
        case Overlay.AddScene => EditScene.component(router, story, None)
        case Overlay.EditScene(scene) =>
          EditScene.component(router, story, Some(scene))
      }

      <.div(
        <.h2("Edit your story"),
        Loading.suspend(router, loadItems.map(items =>
          <.div(^.className := "overlay-anchor",
            <.div(^.className := "edit-canvas",
              items.map(it =>
                editItem(ItemProps(router, copyItem, deleteItem, story, it))).
                toTagMod
            ),
            <.div(^.className := "add-buttons",
              <.button(
                p._1.setOnClick(pages.AddScene(p._2)),
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
      component((router, page.story, Overlay.None)))

  def addScene(page: pages.AddScene, router: pages.Router) =
    Shell.component(router)(
      Banner.component((router, "Alien World.png", "World:"+page.story+"/add")),
      component((router, page.story, Overlay.AddScene)))

  def editScene(page: pages.EditScene, router: pages.Router) =
    Shell.component(router)(
      Banner.component((router, "Alien World.png",
        "World:" + page.story + "/" + page.scene)),
      component((router, page.story, Overlay.EditScene(page.scene))))

  def editQuality(page: pages.EditQuality, router: pages.Router) =
    Shell.component(router)(
      Banner.component((router, "Alien World.png",
        "World:" + page.story + "/~/" + page.quality)),
      component((router, page.story, Overlay.EditQuality(page.quality))))
}
