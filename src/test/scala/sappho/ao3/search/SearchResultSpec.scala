package sappho.ao3.search

import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AnyFunSpec

class SearchResultSpec extends AnyFunSpec with OneInstancePerTest with MockFactory {
  private val browser = JsoupBrowser()
  private val searchPage: Document = browser.parseFile("resources/test/ao3/search.html")
  private val li: Element = searchPage >> element("ol.work.index.group li")

  private val mockBrowser = stub[Browser]
  private val storyPage: Document = browser.parseFile("resources/test/ao3/tmt.html")
  (mockBrowser.get(_: String))
    .when("https://archiveofourown.org/works/23635183?view_adult=true")
    .returns(storyPage.asInstanceOf[mockBrowser.DocumentType])

  private val story = new SearchResult(li, mockBrowser)

  //First search result.
  describe("The Many Times They Met Bunnyx") {
    it("should have the right story id") {
      assertResult(23635183L)(story.storyId)
    }
    it("should have the right title") {
      assertResult("The Many Times They Met Bunnyx")(story.title)
    }
    it("should have the right author") {
      assert(story.authors.map(_.name).contains("SorryJustAnotherPerson"))
    }
    it("should have only one author") {
      assertResult(1)(story.authors.length)
    }
    it("should not be published under a pseudonym") {
      assert(!story.authors.exists(_.isPseud))
    }
    it("should be in English") {
      assertResult("English")(story.language)
    }
    it("should be published on 2020-04-14") {
      assertResult(LocalDate.of(2020, 4, 14))(story.publishedOn)
    }
    it("should be updated on 2020-05-07") {
      assertResult(LocalDate.of(2020, 5, 7))(story.updatedOn)
    }
    it("should not have a completion date") {
      assertResult(None)(story.completedOn)
    }
    it("should have the right word count") {
      assertResult(18226)(story.wordCount)
    }
    it("should have the right score") {
      assertResult(147)(story.score)
    }
    it("should have the right number of views") {
      assertResult(3131)(story.views)
    }
    it("should have 17 chapters") {
      assertResult(17)(story.chapters.length)
    }
    it("should have an unknown planned number of chapters") {
      assertResult(None)(story.chapters.plannedCount)
    }
    it("should have a first chapter with the right title") {
      assertResult("Alix")(story.chapters(0).title)
    }
    it("should be incomplete") {
      assertResult(false)(story.isComplete)
    }
    it("should not be a one-shot") {
      assertResult(false)(story.isOneShot)
    }
  }
}
