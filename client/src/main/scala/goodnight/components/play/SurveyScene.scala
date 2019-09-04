
// package goodnight.components.play

// import japgolly.scalajs.react.vdom.html_<^._
// // import japgolly.scalajs.react.CompScope.DuringCallbackU
// // import japgolly.scalajs.react.ReactComponentB
// // import japgolly.scalajs.react.ReactElement
// // import japgolly.scalajs.react.ReactNode




// object SurveyScene
// {
//   case class Props(
//     title: String,
//     text: String,
//     requirements: String,
//     linkScene: String)

//   type State = Unit

//   type Context = DuringCallbackU[Props, State, Unit]

//   def apply(props: Props, children: ReactNode*): ReactElement =
//     ReactComponentB[Props]("SurveyScene").
//       render(this.render).
//       build(props, children)

//   def render(me: Context) =
//   {
//     <.li(
//       <.img(),
//       <.div(
//         <.h3(me.props.title),
//         me.props.text,
//         me.props.requirements,
//         <.a(^.className := "button action",
//           ^.href := me.props.linkScene,
//           <.span(^.className := "fa fa-share-square-o"),
//           "Go")))
//   }
// }
