package sappho.ao3

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.ao3.Story._

private class PageStory private(val storyId: Long, page: Document) extends Story {
  override def title: String = page >> text("h2.title")
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