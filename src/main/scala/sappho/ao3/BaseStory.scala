package sappho.ao3

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.ao3.tags.{Category, Character, Fandom, Freeform, Rating, Relationship, Warning}
import sappho.tags.{Genre, Tag}

import scala.language.postfixOps

private abstract class BaseStory(val storyId: Long, browser: Browser) extends Story {
  protected def page: Document

  override def title: String = page >> text("h2.title")

  override def authors: Seq[Author] = {
    (page >> elementList("h3 a[rel=author]"))
      .iterator
      .map(a => a attr "href")
      .map(href => Author.fromUrl(href)(browser))
      .toIndexedSeq
  }


  override def tags: Iterable[Tag] = ratings ++ warnings ++ categories ++ genres ++
    fandoms ++ characters ++ relationships ++ freeform

  override def ratings: Iterable[Rating] = (page >> texts("dd.rating.tags a.tag")).map(Rating(_))

  override def warnings: Iterable[Warning] = (page >> texts("dd.warning.tags a.tag")).map(Warning(_))

  override def categories: Iterable[Category] = (page >> texts("dd.category.tags a.tag")).map(Category(_))

  override def genres: Iterable[Genre] = Iterable.empty

  override def fandoms: Iterable[Fandom] = (page >> texts("dd.fandom.tags a.tag")).map(Fandom)

  override def characters: Iterable[Character] = (page >> texts("dd.character.tags a.tag")).map(Character)

  override def relationships: Iterable[Relationship] = (page >> texts("dd.relationship.tags a.tag")).map(Relationship(_))

  override def freeform: Iterable[Freeform] = (page >> texts("dd.freeform.tags a.tag")).map(Freeform)


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

  override def isComplete: Boolean = chapters.plannedCount.contains(chapters.count())
  override def isOneShot: Boolean = chapters.count() == 1 && isComplete



  override def equals(other: Any): Boolean = other match {
    case that: sappho.ao3.Story => this.storyId == that.storyId
    case _ => false
  }

  override def hashCode: Int = storyId##

  override def toString: String = s"sappho.ao3.Story($storyId)"
}