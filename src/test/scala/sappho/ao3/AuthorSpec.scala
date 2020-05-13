package sappho.ao3

import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AnyFunSpec

class AuthorSpec extends AnyFunSpec with OneInstancePerTest with MockFactory {
  private val underlyingBrowser = JsoupBrowser()
  private val mockBrowser = stub[Browser]

  private val mockMap = Map(
    "https://archiveofourown.org/users/Leisey/profile" -> "resources/test/ao3/leisey.html"
  )

  (mockBrowser.get(_: String))
    .when(*)
    .onCall((url: String) => {
      val file = mockMap.getOrElse(url, "resources/test/ao3/poi.html")
      underlyingBrowser.parseFile(file).asInstanceOf[mockBrowser.DocumentType]
    })

  //An author with a multi-paragraph bio and filled-in localization data.
  describe("Leisey's profile") {
    val user = User("Leisey")(mockBrowser)

    it("should have the right name") {
      assertResult("Leisey")(user.name)
    }
    it("should be the base user, not a pseud") {
      assert(!user.isPseud)
    }
    it("should have the correct date of account creation") {
      assertResult(LocalDate.of(2016, 2, 15))(user.joinedOn)
    }
    it("should have a bio") {
      assert(user.bio.isDefined)
    }
    it("should have a multi-paragraph bio") {
      assert(user.bio.get.startsWith("Hi everyone!\n\nI'm"))
    }
    it("should be usable in match expressions") {
      assert(
        user match {
          case User(userName) => userName == "Leisey"
          case _ => false
        }
      )
    }
  }

  //The previous author's pseudonym.
  describe("Mighty_smash's profile") {
    val pseud = Pseud("Leisey", "Mighty_smash")(mockBrowser)

    it("should have the right name") {
      assertResult("Mighty_smash")(pseud.name)
    }
    it("should be a pseud") {
      assert(pseud.isPseud)
    }
    it("should belong to Leisey") {
      assertResult("Leisey")(pseud.user.name)
    }
    it("should have the correct date of account creation") {
      assertResult(LocalDate.of(2016, 2, 15))(pseud.joinedOn)
    }
    it("should have a bio") {
      assert(pseud.bio.isDefined)
    }
    it("should have a multi-paragraph bio") {
      assert(pseud.bio.get.startsWith("Hi everyone!\n\nI'm"))
    }
    it("should be usable in match expressions") {
      assert(
        pseud match {
          case Pseud(userName, psuedName) => userName == "Leisey" && psuedName == "Mighty_smash"
          case _ => false
        }
      )
    }
  }
}
