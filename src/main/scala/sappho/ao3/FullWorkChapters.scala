package sappho.ao3

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private class FullWorkChapters(story: FullWork, browser: Browser, page: Document)
  extends BaseChapters(page >> text("dd.chapters"))
{
  private lazy val provider = new NavigationChapterInfoProvider(story, browser)

  override def apply(chapterIndex: Int): Chapter = {
    if(chapterIndex < 0 || chapterIndex >= count)
      throw new IndexOutOfBoundsException(chapterIndex)

    val rootElement = page >> element("#chapter-" + (chapterIndex + 1))
    new SectionChapter(story, chapterIndex, rootElement, provider)
  }
}
