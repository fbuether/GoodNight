
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages


object SavingButton {
  case class Props(className: String, icon: String,
    enabled: Boolean, saving: Boolean, action: Callback)

  val component = ScalaComponent.builder[Props]("SavingButton").
    render_PC((props, children) =>
      <.button(
        ^.className := props.className +
          (if (props.saving) " loading" else "") +
          (if (!props.enabled) " locked" else ""),
        ^.onClick --> props.action.when(props.enabled).void,
        (^.disabled := true).when(!props.enabled),
        <.i(^.className :=
          (if (props.saving) "far fa-spin fa-compass label"
          else (props.icon + " label"))),
        children)).
    build

  def render(className: String, icon: String,
    enabled: Boolean, saving: Boolean, action: Callback)(children: VdomNode) =
    component(Props(className, icon, enabled, saving, action))(children)

  def render(className: String, icon: String, enabled: Boolean,
    saving: Boolean): (VdomNode => VdomElement) =
    render(className, icon, enabled, saving, Callback.empty)

  def render(enabled: Boolean, saving: Boolean, action: Callback):
      (VdomNode => VdomElement) =
    render("", "fa fa-check-square", enabled, saving, action)

  def render(enabled: Boolean, saving: Boolean): (VdomNode => VdomElement) =
    render(enabled, saving, Callback.empty)

  def render(icon: String, enabled: Boolean, saving: Boolean,
    action: Callback): (VdomNode => VdomElement) =
    render(icon, enabled, saving, action)

  def render(icon: String, enabled: Boolean, saving: Boolean):
      (VdomNode => VdomElement) =
    render(icon, enabled, saving, Callback.empty)
}
