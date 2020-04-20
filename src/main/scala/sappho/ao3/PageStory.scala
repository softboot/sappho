package sappho.ao3

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.ao3.Story._
import sappho.ao3.tags.{Category, Rating, Warning}
import sappho.tags.{Character, Fandom, Freeform, Genre, Relationship, Tag}

private class PageStory(val storyId: Long, browser: Browser) extends Story {
  private val page: Document = browser.get(urlByStoryId(storyId) + "?view_adult=true")

  override def title: String = page >> text("h2.title")

  override def authors: Seq[Author] = {
    (page >> elementList("h3 a[rel=author]"))
      .iterator
      .map(a => a attr "href")
      .map(href => Author.fromUrl(href))
      .toIndexedSeq
  }


  override def tags: Iterable[Tag] = ratings ++ warnings ++ categories

  override def ratings: Iterable[Rating] = (page >> texts("dd.rating.tags a.tag")).map(Rating(_))

  override def warnings: Iterable[Warning] = (page >> texts("dd.warning.tags a.tag")).map(Warning(_))

  override def categories: Iterable[Category] = (page >> texts("dd.category.tags a.tag")).map(Category(_))

  override def genres: Iterable[Genre] = ???

  override def fandoms: Iterable[Fandom] = ???

  override def characters: Iterable[Character] = ???

  override def relationships: Iterable[Relationship] = ???

  override def freeform: Iterable[Freeform] = ???
  

  override def language: String = page >> text("dd.language")

  override def publishedOn: LocalDate = LocalDate.parse(page >> text("dd.published"), ISO_LOCAL_DATE)
  override def updatedOn: LocalDate = {
    (page >?> text("dd.status")) //If the story is a one-shot, date of last update is omitted.
      .map(date => LocalDate.parse(date, ISO_LOCAL_DATE))
      .getOrElse(publishedOn)
  }
  override def completedOn: Option[LocalDate] = if(isComplete) Some(updatedOn) else None

  override def wordCount: Int = page >> extractor("dd.words", text, asInt)
  override def score: Int = (page >?> extractor("dd.kudos", text, asInt)).getOrElse(0)
  override def views: Int = page >> extractor("dd.hits", text, asInt)

  override def isComplete: Boolean = chapters.plannedCount.contains(chapters.length)
  override def isOneShot: Boolean = chapters.length == 1 && isComplete

  override lazy val chapters: Chapters = {
    (page >> text("dd.chapters")) match {
      case "1/1" => new OneShotChapters(this, browser, page)
      case _ => new MultipleChapters(this, browser, page)
    }
  }
}