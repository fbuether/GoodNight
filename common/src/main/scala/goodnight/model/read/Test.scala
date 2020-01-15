
package goodnight.model.read

import goodnight.model.Expression.BinaryOperator


case class Test(
  quality: Quality,
  // def order: Int // the order in which tests should be shown
  succeeded: Boolean, // did the current player pass this test?
  description: String) // the test as a string representation
