package sappho.ao3

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.scalatest.funspec.AnyFunSpec

class ChapterSpec extends AnyFunSpec {
  private implicit val browser: Browser = JsoupBrowser()

  private def load(storyId: Long, fileName: String): Story = {
    val page = browser.parseFile("resources/test/ao3/" + fileName)
    PageStory.makePreloaded(storyId, page)
  }

  //Complete multi-chapter work.
  describe("Powers of Invisibility") {
    val story = load(7094683L, "poi.html")

    it("should have the right number of chapters") {
      assertResult(31)(story.chapters.count)
    }
    it("should have the right planned number of chapters") {
      assertResult(Some(31))(story.chapters.plannedCount)
    }

    describe("Chapter 1") {
      it("should have the right title") {
        assertResult("Same Old, Same Old...")(story.chapters(0).title)
      }
    }
    describe("Chapter 3") {
      it("should have the right title") {
        assertResult("Mad as a Hatter")(story.chapters(2).title)
      }
    }
    describe("Final chapter") {
      it("should have the right title") {
        assertResult("Something New")(story.chapters(30).title)
      }
    }
  }

  //Incomplete multi-chapter work.
  describe("Lost In The Time Stream") {
    val story = load(7077178L, "litts.html")

    it("should have the right number of chapters") {
      assertResult(11)(story.chapters.count)
    }
    it("should have an unknown planned number of chapters") {
      assertResult(None)(story.chapters.plannedCount)
    }

    describe("Chapter 1") {
      it("should have the right title") {
        assertResult("Prologue")(story.chapters(0).title)
      }
    }
    describe("Chapter 3") {
      it("should have the right title") {
        assertResult("Chapter 2")(story.chapters(2).title)
      }
    }
    describe("Last updated chapter") {
      it("should have the right title") {
        assertResult("Chapter 10")(story.chapters(10).title)
      }
    }
  }

  //One-shot.
  describe("Living Arrangements") {
    val story = load(23191339L, "la.html")

    it("should have one chapter") {
      assertResult(1)(story.chapters.count)
    }
    it("should have one planned chapter") {
      assertResult(Some(1))(story.chapters.plannedCount)
    }

    describe("The only chapter") {
      it("should have the right title") {
        assertResult("Chapter 1")(story.chapters(0).title)
      }
    }
  }
}
