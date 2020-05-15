package sappho.ao3

import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AnyFunSpec

class ChapterSpec extends AnyFunSpec with OneInstancePerTest with MockFactory {
  private val underlyingBrowser = JsoupBrowser()
  private val mockBrowser = stub[Browser]

  private val mockMap = Map(
    "https://archiveofourown.org/works/7094683?view_adult=true" -> "resources/test/ao3/poi.html",
    "https://archiveofourown.org/works/7094683/chapters/16279601?view_adult=true" -> "resources/test/ao3/poi3.html",
    "https://archiveofourown.org/works/7094683/chapters/22048766?view_adult=true" -> "resources/test/ao3/poif.html",
    "https://archiveofourown.org/works/7077178?view_adult=true" -> "resources/test/ao3/litts.html",
    "https://archiveofourown.org/works/23191339?view_adult=true" -> "resources/test/ao3/la.html",
    "https://archiveofourown.org/works/7094683/navigate" -> "resources/test/ao3/poi-nav.html",
    "https://archiveofourown.org/works/7077178/navigate" -> "resources/test/ao3/litts-nav.html",
    "https://archiveofourown.org/works/23191339/navigate" -> "resources/test/ao3/la-nav.html"
  )

  (mockBrowser.get(_: String))
    .when(*)
    .onCall((url: String) => {
      val file = mockMap.getOrElse(url, "resources/test/ao3/poi.html")
      underlyingBrowser.parseFile(file).asInstanceOf[mockBrowser.DocumentType]
    })

  //Complete multi-chapter work.
  describe("Powers of Invisibility") {
    val story = new PageStory(7094683, mockBrowser)

    it("should have the right number of chapters") {
      assertResult(31)(story.chapters.count())
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

  //Incomplete multi-chapter work.
  describe("Lost In The Time Stream") {
    val story = new PageStory(7077178, mockBrowser)

    it("should have the right number of chapters") {
      assertResult(11)(story.chapters.count())
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

  //One-shot.
  describe("Living Arrangements") {
    val story = new PageStory(23191339, mockBrowser)

    it("should have one chapter") {
      assertResult(1)(story.chapters.count())
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
