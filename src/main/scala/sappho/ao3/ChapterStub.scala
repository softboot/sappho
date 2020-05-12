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



  override def equals(other: Any): Boolean = other match {
    case that: sappho.ao3.Chapter =>
      this.story.storyId == that.story.storyId &&
        this.chapterIndex == that.chapterIndex
    case _ => false
  }

  override def hashCode(): Int = 31 * story.hashCode + chapterIndex

  override def toString: String = s"sappho.ao3.Chapter(storyId=${story.storyId}, chapterIndex=$chapterIndex)"
}
