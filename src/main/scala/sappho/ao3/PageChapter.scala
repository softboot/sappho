package sappho.ao3

import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private class PageChapter(
    val story: Story,
    val chapterIndex: Int,
    val chapterId: Long,
    page: Document,
    provider: ChapterInfoProvider)
  extends Chapter {

  override def title: String = (page >> element("h3.title"))
    .childNodes
    .collect { case t: TextNode => t.content.strip() }
    .filterNot(_.isEmpty)
    .map(s => s.substring(2))
    .headOption
    .getOrElse("Chapter " + (chapterIndex + 1))

  override def postedOn: LocalDate = provider.pollDate(chapterIndex)

  override def paragraphs: Iterable[String] = (page >> texts("div[role=article] p"))
}

private object PageChapter {
  def load(story: Story, chapterIndex: Int, browser: Browser, provider: ChapterInfoProvider): PageChapter = {
    val chapterId = provider.pollChapterId(chapterIndex)
    val page = browser.get(Chapter.urlById(story.storyId, chapterId) + "?view_adult=true")
    new PageChapter(story, chapterIndex, chapterId, page, provider)
  }

  def first(story: Story, page: Document, provider: ChapterInfoProvider): PageChapter = {
    val chapterIndex = 0
    val chapterId = provider.pollChapterId(chapterIndex)
    new PageChapter(story, chapterIndex, chapterId, page, provider)
  }
}
