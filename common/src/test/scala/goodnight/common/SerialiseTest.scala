
package goodnight.common

import org.scalatest._

import goodnight.model.read
import goodnight.model.Expression
import goodnight.model.Expression.BinaryOperator
import goodnight.common.Serialise._


class SerialiseTest extends FunSpec {
  describe("read.StoryState") {
    it("can be serialised without exception") {
      val testData: read.StoryState = (read.Story("das-schloss",
        "Das Schloss",
        "fbuether",
        "Newer Looking, But Older Rocket.png"),
        Some((read.Player("fbuether",
          "das-schloss",
          "Sir Archibald"),
          Seq(
            read.State(
              read.Quality.Bool("das-schloss",
                "fleissig",
                "Fleißig",
                "I can help you my son, I am Paddle Paul..png"),
              true),
            read.State(
              read.Quality.Integer("das-schloss",
                "fleissig TV",
                "Fleißig TV",
                "Plasma TV.png"),
              11),
            read.State(
              read.Quality.Integer("das-schloss",
                "gut-situiert",
                "Gut situiert",
                "Chea.png"),
              7)),
          read.Activity("das-schloss",
            "fbuether",
            "abwarten",
            Seq(
              read.State(read.Quality.Integer("das-schloss",
                "gut-situiert",
                "Gut situiert",
                "Chea.png"),
                7))),
          read.Scene("das-schloss",
            "abwarten",
            "# Erstmal abwarten.\n\nIrgendetwas wird schon passieren.",
            Seq(
              read.Choice("abwarten-continue",
                "Mal sehen, yes?",
                true,
                Seq(
                  read.Test(
                    read.Quality.Integer("das-schloss",
                      "gierig",
                      "Gierig",
                      "Bomb.png"),
                    true,
                    "Gierig more than 5"),
                  read.Test(
                    read.Quality.Bool("das-schloss",
                      "fleissig",
                      "Fleißig",
                      "Blue Soap.png"),
                    true,
                    "Fleißig")
                )),
              read.Choice("abwarten-hesitate",
                "# Wirklich abwarten?\nBist Du Dir ganz sicher?",
                false,
                Seq(
                  read.Test(
                    read.Quality.Bool("das-schloss",
                      "unerfuellbar",
                      "Unerfüllbar",
                      "Tree.png"),
                    false,
                    "Unerfüllbar")))
            )))))

      assert(write(testData) ==
        """[{"urlname":"das-schloss","name":"Das Schloss","creator":"fbuether","image":"Newer Looking, But Older Rocket.png"},[[{"user":"fbuether","story":"das-schloss","name":"Sir Archibald"},[{"$type":"State.Bool","quality":{"$type":"Quality.Bool","story":"das-schloss","urlname":"fleissig","name":"Fleißig","image":"I can help you my son, I am Paddle Paul..png"},"value":true},{"$type":"State.Integer","quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"fleissig TV","name":"Fleißig TV","image":"Plasma TV.png"},"value":11},{"$type":"State.Integer","quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"gut-situiert","name":"Gut situiert","image":"Chea.png"},"value":7}],{"story":"das-schloss","user":"fbuether","scene":"abwarten","effects":[{"$type":"State.Integer","quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"gut-situiert","name":"Gut situiert","image":"Chea.png"},"value":7}]},{"story":"das-schloss","urlname":"abwarten","text":"# Erstmal abwarten.\n\nIrgendetwas wird schon passieren.","choices":[{"urlname":"abwarten-continue","text":"Mal sehen, yes?","available":true,"tests":[{"quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"gierig","name":"Gierig","image":"Bomb.png"},"succeeded":true,"description":"Gierig more than 5"},{"quality":{"$type":"Quality.Bool","story":"das-schloss","urlname":"fleissig","name":"Fleißig","image":"Blue Soap.png"},"succeeded":true,"description":"Fleißig"}]},{"urlname":"abwarten-hesitate","text":"# Wirklich abwarten?\nBist Du Dir ganz sicher?","available":false,"tests":[{"quality":{"$type":"Quality.Bool","story":"das-schloss","urlname":"unerfuellbar","name":"Unerfüllbar","image":"Tree.png"},"succeeded":false,"description":"Unerfüllbar"}]}]}]]]""")
    }
  }

  describe("parts of StoryState can be parsed") {
    it("can parse the story") {
      assert(write(read.Story("das-schloss",
        "Das Schloss",
        "fbuether",
        "Newer Looking, But Older Rocket.png")) ==
        """{"urlname":"das-schloss","name":"Das Schloss","creator":"fbuether","image":"Newer Looking, But Older Rocket.png"}""")
    }

    it("can parse the player") {
      assert(write(read.Player("fbuether",
        "das-schloss",
        "Sir Archibald")) ==
        """{"user":"fbuether","story":"das-schloss","name":"Sir Archibald"}""")
    }

    it("can be a quality") {
      assert(write(read.Quality("das-schloss",
                "fleissig",
                read.Sort.Bool,
                "Fleißig",
                "I can help you my son, I am Paddle Paul..png")) ==
        """{"$type":"Quality.Bool","story":"das-schloss","urlname":"fleissig","name":"Fleißig","image":"I can help you my son, I am Paddle Paul..png"}""")
    }

    it("can be a state") {
      assert(write(read.State(read.Quality.Bool("das-schloss",
        "fleissig",
        "Fleißig",
        "I can help you my son, I am Paddle Paul..png"),
        true)) ==
        """{"$type":"State.Bool","quality":{"$type":"Quality.Bool","story":"das-schloss","urlname":"fleissig","name":"Fleißig","image":"I can help you my son, I am Paddle Paul..png"},"value":true}""")
    }

    it("can be another state") {
      assert(write(read.State(
        read.Quality.Integer("das-schloss",
          "fleissig TV",
          "Fleißig TV",
          "Plasma TV.png"),
        11)) ==
        """{"$type":"State.Integer","quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"fleissig TV","name":"Fleißig TV","image":"Plasma TV.png"},"value":11}""")
    }

    it("can be even yet another state") {
      assert(write(read.State(read.Quality.Integer("das-schloss",
            "gut-situiert",
            "Gut situiert",
            "Chea.png"),
            7)) ==
        """{"$type":"State.Integer","quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"gut-situiert","name":"Gut situiert","image":"Chea.png"},"value":7}""")
    }

    it("can be an activity without state") {
      assert(write(read.Activity("das-schloss",
        "fbuether",
        "abwarten",
        Seq())) ==
        """{"story":"das-schloss","user":"fbuether","scene":"abwarten","effects":[]}""")
    }

    it("can be a sequence of one state") {
      assert(write(Seq(
          read.State(read.Quality.Integer("das-schloss",
            "gut-situiert",
            "Gut situiert",
            "Chea.png"),
            7))) ==
        """[{"$type":"State.Integer","quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"gut-situiert","name":"Gut situiert","image":"Chea.png"},"value":7}]""")
    }

    it("can be an activity") {
      assert(write(read.Activity("das-schloss",
        "fbuether",
        "abwarten",
        Seq(
          read.State(read.Quality.Integer("das-schloss",
            "gut-situiert",
            "Gut situiert",
            "Chea.png"),
            7)))) ==
        """{"story":"das-schloss","user":"fbuether","scene":"abwarten","effects":[{"$type":"State.Integer","quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"gut-situiert","name":"Gut situiert","image":"Chea.png"},"value":7}]}""")
    }

    it("can be a test") {
      assert(write(read.Test(
        read.Quality.Bool("das-schloss",
          "fleissig",
          "Fleißig",
          "Blue Soap.png"),
        true,
        "Fleißig")) ==
        """{"quality":{"$type":"Quality.Bool","story":"das-schloss","urlname":"fleissig","name":"Fleißig","image":"Blue Soap.png"},"succeeded":true,"description":"Fleißig"}""")
    }

    it("can be a choice") {
      assert(write(read.Choice("abwarten-continue",
        "Mal sehen, yes?",
        true,
        Seq(
          read.Test(
            read.Quality.Integer("das-schloss",
              "gierig",
              "Gierig",
              "Bomb.png"),
            true,
            "Gierig at least 5"),
          read.Test(
            read.Quality.Bool("das-schloss",
              "fleissig",
              "Fleißig",
              "Blue Soap.png"),
            true,
            "Fleißig")))) ==
        """{"urlname":"abwarten-continue","text":"Mal sehen, yes?","available":true,"tests":[{"quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"gierig","name":"Gierig","image":"Bomb.png"},"succeeded":true,"description":"Gierig at least 5"},{"quality":{"$type":"Quality.Bool","story":"das-schloss","urlname":"fleissig","name":"Fleißig","image":"Blue Soap.png"},"succeeded":true,"description":"Fleißig"}]}""")
    }

    it("can be a scene") {
      assert(write(read.Scene("das-schloss",
        "abwarten",
        "# Erstmal abwarten.\n\nIrgendetwas wird schon passieren.",
        Seq(
          read.Choice("abwarten-continue",
            "Mal sehen, yes?",
            true,
            Seq(
              read.Test(
                read.Quality.Integer("das-schloss",
                  "gierig",
                  "Gierig",
                  "Bomb.png"),
                true,
                "Gierig at least 5"),
              read.Test(
                read.Quality.Bool("das-schloss",
                  "fleissig",
                  "Fleißig",
                  "Blue Soap.png"),
                true,
                "Fleißig")))))) ==
        """{"story":"das-schloss","urlname":"abwarten","text":"# Erstmal abwarten.\n\nIrgendetwas wird schon passieren.","choices":[{"urlname":"abwarten-continue","text":"Mal sehen, yes?","available":true,"tests":[{"quality":{"$type":"Quality.Integer","story":"das-schloss","urlname":"gierig","name":"Gierig","image":"Bomb.png"},"succeeded":true,"description":"Gierig at least 5"},{"quality":{"$type":"Quality.Bool","story":"das-schloss","urlname":"fleissig","name":"Fleißig","image":"Blue Soap.png"},"succeeded":true,"description":"Fleißig"}]}]}""")
    }
  }
}
