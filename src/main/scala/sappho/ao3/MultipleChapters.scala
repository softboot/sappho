package sappho.ao3

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private class MultipleChapters(story: Story, page: Document) extends Chapters {
  private val progress = (page >> text("dd.chapters")).split("/")
  private val provider = new DropdownListChapterInfoProvider(page)

  override def count: Int = progress(0).toInt
  override def plannedCount: Option[Int] = progress(1).toIntOption

  override def apply(chapterIndex: Int): Chapter = {
    if(chapterIndex < 0 || chapterIndex >= count)
      throw new IndexOutOfBoundsException(chapterIndex)
    new ChapterStub(story, chapterIndex, provider)
  }
}
