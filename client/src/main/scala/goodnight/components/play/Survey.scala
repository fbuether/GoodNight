
// package goodnight.components

// import japgolly.scalajs.react.vdom.html_<^._
// import japgolly.scalajs.react.ReactComponentB


// case class SurveyProps(
//   player: String,
//   location: String,
//   scenes: List[String])


// object Survey
// {
//   val mk = ReactComponentB[SurveyProps]("Survey").
//   render(c =>
//     <.div(
//       <.h2(c.props.player + ", welcome in " + c.props.location),
//       <.p("These options are currently available to you."),
//       <.ul(^.className := "scenes",
//         c.props.scenes.map(c => <.span(c))))).
//   build
// }
