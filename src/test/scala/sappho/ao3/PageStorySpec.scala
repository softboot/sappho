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
  }
}
