
package goodnight.stories

import java.util.UUID
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object ReadScene {
  case class Props(router: pages.Router, player: model.Player,
    scene: model.Scene, choices: Seq[model.Choice],
    onSelect: model.Choice => Callback)

  def component = ScalaComponent.builder[Props]("SceneAction").
    stateless.
    render_P(props =>
      <.div(
        <.h2(props.scene.title),
        <.p(props.scene.text),
        <.ul(^.className := "as-items",
          props.choices.map(choice =>
            <.li(
              <.p(choice.text))
          ).toTagMod
        ))
    ).
    build


  // def doSceneAction(router: pages.Router,
  //   story: model.Story, player: model.Player, scene: model.Scene,
  //   onSelectChoice: model.Choice => Callback):
  //     AsyncCallback[VdomElement] = {
  //   Request(ApiV1.DoScene, story.urlname, scene.urlname).send.
  //     forStatus(200).forJson[(model.Scene, Seq[model.Choice])].
  //     body.attemptTry.map({
  //       case Success((scene, choices)) =>
  //         component(Props(router, player, scene, choices, onSelectChoice))
  //       case Failure(e) =>
  //         Error.component(e, false)
  //     })
  // }
}

