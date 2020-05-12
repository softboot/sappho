package sappho.ao3

import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AnyFunSpec

class FullWorkSpec extends AnyFunSpec with OneInstancePerTest with MockFactory {
  private val underlyingBrowser = JsoupBrowser()
  private val mockBrowser = stub[Browser]

  private val mockMap = Map(
    "https://archiveofourown.org/works/7094683?view_adult=true" -> "resources/test/ao3/poi.html",
    "https://archiveofourown.org/works/7094683?view_full_work=true&view_adult=true" -> "resources/test/ao3/poi-full.html",
    "https://archiveofourown.org/works/7094683/navigate" -> "resources/test/ao3/poi-nav.html",

    "https://archiveofourown.org/works/7077178?view_adult=true" -> "resources/test/ao3/litts.html",
    "https://archiveofourown.org/works/7077178?view_full_work=true&view_adult=true" -> "resources/test/ao3/litts-full.html",
    "https://archiveofourown.org/works/7077178/navigate" -> "resources/test/ao3/litts-nav.html",

    "https://archiveofourown.org/works/23191339?view_adult=true" -> "resources/test/ao3/la.html",
    "https://archiveofourown.org/works/23191339/navigate" -> "resources/test/ao3/la-nav.html"
  )

  (mockBrowser.get(_: String))
    .when(*)
    .onCall((url: String) => {
      val file = mockMap.getOrElse(url, "resources/test/ao3/poi.html")
      underlyingBrowser.parseFile(file).asInstanceOf[mockBrowser.DocumentType]
    })

  //Complete multi-chapter work by one author.
  describe("Powers of Invisibility") {
    val id = 7094683L
    val story = new PageStory(id, mockBrowser).fullWork

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
    it("should be complete") {
      assertResult(true)(story.isComplete)
    }
    it("should not be a one-shot") {
      assertResult(false)(story.isOneShot)
    }
    it("should have the right number of chapters") {
      assertResult(31)(story.chapters.length)
    }
    it("should have the right planned number of chapters") {
      assertResult(Some(31))(story.chapters.plannedCount)
    }
    it("should have 100% progress") {
      assertResult(Some(1.0))(story.chapters.progress)
    }

    describe("Chapter 1") {
      it("should have the right title") {
        assertResult("Same Old, Same Old...")(story.chapters(0).title)
      }
      it("should have been posted on 2016-06-05") {
        assertResult(LocalDate.of(2016, 6, 5))(story.chapters(0).postedOn)
      }
      it("should have the right first paragraph (head)") {
        assertResult("It looked like boredom.")(story.chapters(0).paragraphs.head)
      }
    }
    describe("Chapter 3") {
      it("should have the right title") {
        assertResult("Mad as a Hatter")(story.chapters(2).title)
      }
      it("should have been posted on 2016-06-11") {
        assertResult(LocalDate.of(2016, 6, 11))(story.chapters(2).postedOn)
      }
      it("should have the right sixth paragraph (further iteration)") {
        assertResult("A derby hat.")(story.chapters(2).paragraphs.drop(5).head)
      }
    }
    describe("Final chapter") {
      it("should have the right title") {
        assertResult("Something New")(story.chapters(30).title)
      }
      it("should have been posted on 2017-02-19") {
        assertResult(LocalDate.of(2017, 2, 19))(story.chapters(30).postedOn)
      }
      it("should have the right fourth paragraph (italics ignored)") {
        assertResult("What’s her name?")(story.chapters(30).paragraphs.drop(3).head)
      }
    }
  }

  //Incomplete multi-chapter collaboration.
  describe("Lost In The Time Stream") {
    val story = new PageStory(7077178, mockBrowser).fullWork

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
    it("should not be complete") {
      assertResult(false)(story.isComplete)
    }
    it("should not be a one-shot") {
      assertResult(false)(story.isOneShot)
    }
    it("should have the right number of chapters") {
      assertResult(11)(story.chapters.length)
    }
    it("should have an unknown planned number of chapters") {
      assertResult(None)(story.chapters.plannedCount)
    }
    it("should have unknown progress") {
      assertResult(None)(story.chapters.progress)
    }

    describe("Chapter 1") {
      it("should have the right title") {
        assertResult("Prologue")(story.chapters(0).title)
      }
      it("should have been posted on 2016-06-03") {
        assertResult(LocalDate.of(2016, 6, 3))(story.chapters(0).postedOn)
      }
    }
    describe("Chapter 3") {
      it("should have the right title") {
        assertResult("Chapter 2")(story.chapters(2).title)
      }
      it("should have been posted on 2016-06-18") {
        assertResult(LocalDate.of(2016, 6, 18))(story.chapters(2).postedOn)
      }
    }
    describe("Last updated chapter") {
      it("should have the right title") {
        assertResult("Chapter 10")(story.chapters(10).title)
      }
      it("should have been posted on 2018-06-24") {
        assertResult(LocalDate.of(2018, 6, 24))(story.chapters(10).postedOn)
      }
    }
  }

  //One-shot published under a pseudonym.
  describe("Living Arrangements") {
    val story = new PageStory(23191339, mockBrowser).fullWork

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
    it("should be complete") {
      assertResult(true)(story.isComplete)
    }
    it("should be a one-shot") {
      assertResult(true)(story.isOneShot)
    }
    it("should have one chapter") {
      assertResult(1)(story.chapters.length)
    }
    it("should have one planned chapter") {
      assertResult(Some(1))(story.chapters.plannedCount)
    }
    it("should have 100% progress") {
      assertResult(Some(1.0))(story.chapters.progress)
    }

    describe("The only chapter") {
      it("should have the right title") {
        assertResult("Chapter 1")(story.chapters(0).title)
      }
      it("should have been posted on 2020-03-17") {
        assertResult(LocalDate.of(2020, 3, 17))(story.chapters(0).postedOn)
      }
      it("should have the right tenth paragraph") {
        assertResult("“I, um… okay…”")(story.chapters(0).paragraphs.drop(9).head)
      }
    }
  }
}
