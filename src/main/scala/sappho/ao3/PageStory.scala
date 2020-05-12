package sappho.ao3

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.ao3.Story._
import sappho.util.Log._

private class PageStory(storyId: Long, browser: Browser) extends BaseStory(storyId, browser) with StrictLogging {
  override protected val page: Document = browser.get(urlByStoryId(storyId) + "?view_adult=true", logger)

  override lazy val chapters: Chapters = {
    (page >> text("dd.chapters")) match {
      case "1/1" => new OneShotChapters(this, browser, page)
      case _ => new MultipleChapters(this, browser, page)
    }
  }

  override def fullWork: Story = if(isOneShot) this else new FullWork(storyId, browser)
}