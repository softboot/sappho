package sappho.ao3

import com.typesafe.scalalogging.Logger
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.model._
import sappho.util.Log._

private class PageChapter(
     story: Story,
     chapterIndex: Int,
     override val chapterId: Long,
     page: Document,
     provider: ChapterInfoProvider)
  extends SectionChapter(story, chapterIndex, page.body, provider)

private object PageChapter {
  private val logger = Logger[PageChapter]

  def load(story: Story, chapterIndex: Int, browser: Browser, provider: ChapterInfoProvider): PageChapter = {
    val chapterId = provider.pollChapterId(chapterIndex)
    val page = browser.get(Chapter.urlById(story.storyId, chapterId).toString + "?view_adult=true", logger)
    new PageChapter(story, chapterIndex, chapterId, page, provider)
  }

  def first(story: Story, page: Document, provider: ChapterInfoProvider): PageChapter = {
    val chapterIndex = 0
    val chapterId = provider.pollChapterId(chapterIndex)
    new PageChapter(story, chapterIndex, chapterId, page, provider)
  }
}
