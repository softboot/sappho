package sappho.ao3
import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.Browser

private class ChapterStub(
     val story: Story,
     val chapterIndex: Int,
     browser: Browser,
     provider: ChapterInfoProvider)
  extends Chapter {

  override def chapterId: Long = provider.pollChapterId(chapterIndex)
  override def title: String = provider.pollTitle(chapterIndex)
  override def postedOn: LocalDate = provider.pollDate(chapterIndex)

  override def paragraphs: Iterable[String] = pageChapter.paragraphs
  override def fullText: String = pageChapter.fullText

  private lazy val pageChapter = PageChapter.load(story, chapterIndex, browser, provider)
}
