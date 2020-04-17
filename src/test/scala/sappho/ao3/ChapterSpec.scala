package sappho.ao3

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec

class ChapterSpec extends AnyFunSpec with MockFactory {
  private val underlyingBrowser = JsoupBrowser()
  private val mockBrowser: Browser = stub[Browser]
  mockUrl("https://archiveofourown.org/works/7094683?view_adult=true", "resources/test/ao3/poi.html")
  mockUrl("https://archiveofourown.org/works/7077178?view_adult=true", "resources/test/ao3/litts.html")
  mockUrl("https://archiveofourown.org/works/23191339?view_adult=true", "resources/test/ao3/la.html")

  //Complete multi-chapter work.
  describe("Powers of Invisibility") {
    val story = PageStory.load(7094683, mockBrowser)

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
    val story = PageStory.load(7077178, mockBrowser)

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
    val story = PageStory.load(23191339, mockBrowser)

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



  // Bind URL addresses to test file paths
  private def mockUrl(url: String, file: String): Unit = {
    val page = underlyingBrowser.parseFile(file)
      .asInstanceOf[ChapterSpec.this.mockBrowser.DocumentType]
    (mockBrowser get _) when url returns page
  }
}
