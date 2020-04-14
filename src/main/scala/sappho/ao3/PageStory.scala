package sappho.ao3

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.ao3.Story._

private class PageStory private(val storyId: Long, page: Document) extends Story {
  override def title: String = page >> text("h2.title")
  override def language: String = page >> text("dd.language")

  override def wordCount: Int = page >> extractor("dd.words", text, asInt)
  override def score: Int = page >> extractor("dd.kudos", text, asInt)
  override def views: Int = page >> extractor("dd.hits", text, asInt)

  override def chapterCount: Int = progress(0).toInt
  override def plannedChapterCount: Option[Int] = progress(1).toIntOption
  override def isComplete: Boolean = progress(0) == progress(1)
  override def isOneShot: Boolean = chapterCount == 1 && isComplete


  private lazy val progress = (page >> text("dd.chapters")).split("/")
}

private object PageStory {
  def load(storyId: Long, browser: Browser): PageStory = {
    def url = urlByStoryId(storyId)
    def page = browser.get(url.toString)
    makePreloaded(storyId, page)
  }

  def makePreloaded(storyId: Long, preloadedPage: Document): PageStory = {
    new PageStory(storyId, preloadedPage)
  }
}