package sappho.ao3

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.model._
import sappho.ao3.Story._
import sappho.util.Log._

private class FullWork(storyId: Long, browser: Browser) extends BaseStory(storyId, browser) with StrictLogging {
  override protected val page: Document = browser.get(urlByStoryId(storyId) + "?view_full_work=true&view_adult=true", logger)

  override val chapters: Chapters = new FullWorkChapters(this, browser, page)

  override def fullWork: Story = this
}