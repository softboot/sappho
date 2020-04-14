package sappho.ao3
import java.net.URL

import net.ruippeixotog.scalascraper.browser.Browser

object Archive extends sappho.Archive {
  override def name: String = "Archive of Our Own"

  override def abbreviation: String = "AO3"

  override def homepage: URL = new URL("https://archiveofourown.org/")

  override def fetchStoryById(storyId: Long)(implicit browser: Browser): sappho.Story = {
    PageStory.load(storyId, browser)
  }
}
