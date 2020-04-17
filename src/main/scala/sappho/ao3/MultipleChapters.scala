package sappho.ao3

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private class MultipleChapters(story: Story, browser: Browser, page: Document) extends Chapters {
  private val progressText = (page >> text("dd.chapters")).split("/")
  private lazy val provider = new DropdownListChapterInfoProvider(story, browser, page)

  override def count: Int = progressText(0).toInt
  override def plannedCount: Option[Int] = progressText(1).toIntOption

  override def apply(chapterIndex: Int): Chapter = {
    if(chapterIndex < 0 || chapterIndex >= count)
      throw new IndexOutOfBoundsException(chapterIndex)

    if(chapterIndex == 0)
      PageChapter.first(story, page, provider)

    new ChapterStub(story, chapterIndex, browser, provider)
  }
}
