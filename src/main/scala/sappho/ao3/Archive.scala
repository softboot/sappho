package sappho.ao3
import java.net.URL

import net.ruippeixotog.scalascraper.browser.Browser
import sappho.ao3.search.QueryExecutor
import sappho.queries.{Order, Query}

object Archive extends sappho.Archive {
  override def name: String = "Archive of Our Own"

  override def abbreviation: String = "AO3"

  override def homepage: URL = new URL("https://archiveofourown.org/")

  override def fetchStoryById(storyId: Long)(implicit browser: Browser): Story = {
    new PageStory(storyId, browser)
  }

  override def search(query: Query, order: Order[Any])(implicit browser: Browser): IterableOnce[Story] = {
    QueryExecutor.executeQuery(query, order).filter(query)
  }
}
