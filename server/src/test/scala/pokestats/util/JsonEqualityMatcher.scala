package pokestats.util

import org.scalactic.Prettifier
import org.scalatest.matchers.{MatchResult, Matcher}
import org.skyscreamer.jsonassert.{JSONCompare, JSONCompareMode}

trait JsonEqualityMatcher {
  def equalsToJson(spread: String): Matcher[String] = {
    new Matcher[String] {
      def apply(left: String): MatchResult = {
        val result =
          JSONCompare.compareJSON(left, spread, JSONCompareMode.STRICT)
        MatchResult(
          !result.failed(),
          result.getMessage,
          "jsons are equal"
        )
      }
      override def toString: String =
        "jsonEqual (" + Prettifier.default(spread) + ")"
    }
  }
}
