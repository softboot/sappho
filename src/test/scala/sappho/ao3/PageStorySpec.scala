package sappho.ao3

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.scalatest.funspec.AnyFunSpec

class PageStorySpec extends AnyFunSpec {
  implicit val browser: Browser = JsoupBrowser()

  describe("Powers of Invisibility") {
    val id = 7094683L
    val page = browser.parseFile("resources/test/ao3/poi.html")
    val story = sappho.ao3.PageStory.makePreloaded(id, page)

    it("should have the right story id") {
      assertResult(id)(story.storyId)
    }
    it("should have the right title") {
      assertResult("Powers of Invisibility")(story.title)
    }
    it("should have the right word count") {
      assertResult(246310)(story.wordCount)
    }
    it("should have the right score") {
      assertResult(1378)(story.score)
    }
    it("should have the right number of views") {
      assertResult(40660)(story.views)
    }
    it("should have the right number of chapters") {
      assertResult(31)(story.chapterCount)
    }
    it("should have the right planned number of chapters") {
      assertResult(Some(31))(story.plannedChapterCount)
    }
    it("should be complete") {
      assertResult(true)(story.isComplete)
    }
    it("should not be a one-shot") {
      assertResult(false)(story.isOneShot)
    }
  }
}
