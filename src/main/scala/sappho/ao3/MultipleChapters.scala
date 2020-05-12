package sappho.ao3

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private class MultipleChapters(story: Story, browser: Browser, page: Document)
  extends BaseChapters(page >> text("dd.chapters"))
{
  private lazy val provider = new DropdownListChapterInfoProvider(story, browser, page)

  override def apply(chapterIndex: Int): Chapter = {
    if(chapterIndex < 0 || chapterIndex >= count)
      throw new IndexOutOfBoundsException(chapterIndex)

    if(chapterIndex == 0)
      PageChapter.first(story, page, provider)

    new ChapterStub(story, chapterIndex, browser, provider)
  }
}
