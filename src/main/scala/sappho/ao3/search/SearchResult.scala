package sappho.ao3.search

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.scraper.ContentParsers._
import sappho.ao3.tags.{Category, Character, Fandom, Freeform, Rating, Relationship, Warning}
import sappho.ao3.{Author, PageStory, Story}
import sappho.tags.{Genre, Tag}

class SearchResult(private[search] val li: Element, browser: Browser) extends Story {
  override def storyId = (li attr "id").substring(5).toLong

  override def title: String = li >> text("h4.heading a")

  override def authors: Seq[Author] = {
    (li >> elementList("h4.heading a[rel=author]"))
      .iterator
      .map(a => a attr "href")
      .map(href => Author.fromUrl(href))
      .toIndexedSeq
  }


  override def tags: Iterable[Tag] = ratings ++ warnings ++ categories ++ genres ++
    fandoms ++ characters ++ relationships ++ freeform

  override def ratings: Iterable[Rating] = (li >> texts("span.rating span.text")).map(Rating(_))

  override def warnings: Iterable[Warning] = (li >> texts("li.warnings a.tag")).map(Warning(_))

  override def categories: Iterable[Category] = (li >> texts("span.category span.text"))
    .flatMap(_.split(", "))
    .map(Category(_))

  override def genres: Iterable[Genre] = Iterable.empty

  override def fandoms: Iterable[Fandom] = (li >> texts("h5.fandoms a.tag")).map(Fandom)

  override def characters: Iterable[Character] = (li >> texts("li.characters a.tag")).map(Character)

  override def relationships: Iterable[Relationship] = (li >> texts("li.relationships a.tag")).map(Relationship(_))

  override def freeform: Iterable[Freeform] = (li >> texts("li.freeforms a.tag")).map(Freeform)


  override def language: String = li >> text("dd.language")

  override def publishedOn: LocalDate = page.publishedOn
  override def updatedOn: LocalDate = LocalDate.parse(li >> text("p.datetime"), SearchResult.DATE_FORMAT)
  override def completedOn: Option[LocalDate] = if(isComplete) Some(updatedOn) else None

  override def wordCount: Int = (li >> text("dd.words"))
    .replaceAll(",", "")
    .toInt
  override def score: Int = (li >?> extractor("dd.kudos", text, asInt)).getOrElse(0)
  override def views: Int = li >> extractor("dd.hits", text, asInt)

  override def isComplete: Boolean = chapters.plannedCount.contains(chapters.length)
  override def isOneShot: Boolean = chapters.length == 1 && isComplete

  override lazy val chapters: Chapters = new Chapters(this)

  private[search] lazy val page: Story = new PageStory(storyId, browser)
}

object SearchResult {
  val DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH)
}