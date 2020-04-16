package sappho.ao3

import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.scalatest.funspec.AnyFunSpec

class PageStorySpec extends AnyFunSpec {
  private implicit val browser: Browser = JsoupBrowser()

  private def load(storyId: Long, fileName: String): Story = {
    val page = browser.parseFile("resources/test/ao3/" + fileName)
    PageStory.makePreloaded(storyId, page)
  }

  //Complete multi-chapter work by one author.
  describe("Powers of Invisibility") {
    val id = 7094683L
    val story = load(id, "poi.html")

    it("should have the right story id") {
      assertResult(id)(story.storyId)
    }
    it("should have the right title") {
      assertResult("Powers of Invisibility")(story.title)
    }
    it("should have the right author") {
      assert(story.authors.map(_.name).contains("yestomiraculous"))
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
    it("should be published on 2016-06-05") {
      assertResult(LocalDate.of(2016, 6, 5))(story.publishedOn)
    }
    it("should be updated on 2017-02-19") {
      assertResult(LocalDate.of(2017, 2, 19))(story.updatedOn)
    }
    it("should be completed on 2017-02-19") {
      assertResult(Some(LocalDate.of(2017, 2, 19)))(story.completedOn)
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

  //Incomplete multi-chapter collaboration.
  describe("Lost In The Time Stream") {
    val story = load(7077178L, "litts.html")

    it("should have the right title") {
      assertResult("Lost In The Time Stream")(story.title)
    }
    it("should have only two authors") {
      assertResult(2)(story.authors.length)
    }
    it("should have the right two authors") {
      val authorSet = story.authors.map(_.name).toSet
      assertResult(Set("Leisey", "wonderfulwizardofthozz"))(authorSet)
    }
    it("should not be published under a pseudonym") {
      assert(!story.authors.exists(_.isPseud))
    }
    it("should be published on 2016-06-03") {
      assertResult(LocalDate.of(2016, 6, 3))(story.publishedOn)
    }
    it("should be updated on 2018-06-24") {
      assertResult(LocalDate.of(2018, 6, 24))(story.updatedOn)
    }
    it("should not have a completion date") {
      assertResult(None)(story.completedOn)
    }
    it("should have the right number of chapters") {
      assertResult(11)(story.chapterCount)
    }
    it("should have an unknown planned number of chapters") {
      assertResult(None)(story.plannedChapterCount)
    }
    it("should not be complete") {
      assertResult(false)(story.isComplete)
    }
    it("should not be a one-shot") {
      assertResult(false)(story.isOneShot)
    }
  }

  //One-shot published under a pseudonym.
  describe("Living Arrangements") {
    val story = load(23191339L, "la.html")

    it("should have the right title") {
      assertResult("Living Arrangements")(story.title)
    }
    it("should have only one author") {
      assertResult(1)(story.authors.length)
    }
    it("should be published by the right user") {
      assertResult("dimensionhoppingrose")(story.authors(0).user.name)
    }
    it("should be published under a pseudonym") {
      assert(story.authors(0).isPseud)
    }
    it("should be published under the correct pseudonym") {
      assertResult("lenasmagic")(story.authors(0).name)
    }
    it("should be published on 2020-03-17") {
      assertResult(LocalDate.of(2020, 3, 17))(story.publishedOn)
    }
    it("should have an update date equal to its publication date") {
      assertResult(LocalDate.of(2020, 3, 17))(story.updatedOn)
    }
    it("should have a completion date equal to its publication date") {
      assertResult(Some(LocalDate.of(2020, 3, 17)))(story.completedOn)
    }
    it("should have one chapter") {
      assertResult(1)(story.chapterCount)
    }
    it("should have one planned chapter") {
      assertResult(Some(1))(story.plannedChapterCount)
    }
    it("should be complete") {
      assertResult(true)(story.isComplete)
    }
    it("should be a one-shot") {
      assertResult(true)(story.isOneShot)
    }
  }
}
