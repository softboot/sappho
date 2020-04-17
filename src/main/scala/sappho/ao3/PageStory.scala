package sappho.ao3

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.ao3.Story._

private class PageStory private(val storyId: Long, page: Document) extends Story {
  override def title: String = page >> text("h2.title")

  override def authors: Seq[Author] = {
    (page >> elementList("h3 a[rel=author]"))
      .iterator
      .map(a => a attr "href")
      .map(href => Author.fromUrl(href))
      .toIndexedSeq
  }

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

  override def isComplete: Boolean = chapters.plannedCount.contains(chapters.count)
  override def isOneShot: Boolean = chapters.count == 1 && isComplete

  override lazy val chapters: Chapters = new MultipleChapters(this, page)
}

private object PageStory {
  def load(storyId: Long, browser: Browser): PageStory = {
    def url = urlByStoryId(storyId) + "?view_adult=true"
    def page = browser.get(url.toString)
    makePreloaded(storyId, page)
  }

  def makePreloaded(storyId: Long, preloadedPage: Document): PageStory = {
    new PageStory(storyId, preloadedPage)
  }
}