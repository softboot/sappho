package sappho.ao3.search

import java.net.URL

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.model._
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AnyFunSpec

class ResultPageSpec extends AnyFunSpec with OneInstancePerTest with MockFactory {
  private val browser = JsoupBrowser()

  describe("First result page") {
    val mockBrowser = stub[Browser]
    val mockUrl = "https://archiveofourown.org/some/search/url"
    val searchPage: Document = browser.parseFile("resources/test/ao3/search.html")
    (mockBrowser.get(_: String)) when mockUrl returns searchPage.asInstanceOf[mockBrowser.DocumentType]

    val resultPage = new ResultPage(new URL(mockUrl), mockBrowser)

    it("should have the right first story") {
      val firstStory = resultPage.head
      assertResult("The Many Times They Met Bunnyx")(firstStory.title)
    }
    it("should have the right second story") {
      val secondStory = resultPage.iterator.drop(1).next()
      assertResult("a miraculous life")(secondStory.title)
    }
    it("should have the right last story") {
      val lastStory = resultPage.last
      assertResult("Mort Finale")(lastStory.title)
    }
    it("should have the right number of stories") {
      assertResult(20)(resultPage.count(_ => true))
    }
  }
}
