
package goodnight.stories

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
  type Props = (pages.Router, String)

  val component = ScalaComponent.builder[Props]("EditStory").
    render_P(p => <.div(
      <.h2("Edit your story"),
      <.div(^.className := "edit-canvas",
        <.div(^.className := "scene",
          <.div(
            <.i(^.className := "fas fa-scroll"),
            <.span("At the docks, there is but not a sound to hear"),
            <.a(^.href := "#",
              ^.alt := "Edit this scene",
              <.i(^.className := "fas fa-pen-fancy")),
            <.a(^.href := "#",
              ^.alt := "Copy this scene",
              <.i(^.className := "far fa-copy")),
            <.a(^.className := "danger",
              ^.href := "#",
              ^.alt := "Delete this scene",
              <.i(^.className := "far fa-trash-alt"))),
          <.p(
            """There is a certain atmosphere at the docks that seems to claw
              at your nose like a hungry sewer rat. People bustle about"""),
          <.div(^.className := "tags",
            <.span("docks"), <.span("intro"), <.span("conflict"))),

        <.div(^.className := "quality",
          <.div(
            <.i(^.className := "fas fa-thumbtack"),
            <.span("Cold, hard cash"),
            <.a(^.href := "#",
              ^.alt := "Edit this quality",
              <.i(^.className := "fas fa-pen-fancy")),
            <.a(^.href := "#",
              ^.alt := "Copy this quality",
              <.i(^.className := "far fa-copy")),
            <.a(^.className := "danger",
              ^.href := "#",
              ^.alt := "Delete this quality",
              <.i(^.className := "far fa-trash-alt"))),
          <.p("""You gotta pay for what you take. That's how it's always
            been.""")),

        <.div(^.className := "location",
          <.div(
            <.i(^.className := "fas fa-map-marked"),
            <.span("The Docks"),
            <.a(^.href := "#",
              ^.alt := "Edit this scene",
              <.i(^.className := "fas fa-pen-fancy")),
            <.a(^.href := "#",
              ^.alt := "Copy this scene",
              <.i(^.className := "far fa-copy")),
            <.a(^.className := "danger",
              ^.href := "#",
              ^.alt := "Delete this scene",
              <.i(^.className := "far fa-trash-alt"))),
          <.p("""The docks have been the hub of trading in this forsaken town.
            They are not, anymore, and even the residents are forced to take
            notice."""),
          <.div(^.className := "tags",
            <.span("docks")))
      ))).
    build

  def loadStory(router: pages.Router, name: String): AsyncCallback[VdomElement] =
    Request(ApiV1.Story, name).send.forJson.map({
      case Reply(200, Success(storyJson)) =>
        this.component(router, name)
      case e =>
        <.div("Error :( -> " + e)
    })


  def render(page: pages.EditStory, router: pages.Router) =
    Shell.component(router)(
      Banner.component((router, "Alien World.png", "World:"+page.name)),
      Loading.suspend(router, loadStory(router, page.name)))
}
