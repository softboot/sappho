package sappho.ao3

import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private class OneShotChapters(story: Story, browser: Browser, page: Document) extends Chapters {
  override def count: Int = 1
  override def plannedCount: Option[Int] = Some(1)
  override def progress: Option[Double] = Some(1.0)

  override def apply(i: Int): Chapter = i match {
    case 0 => onlyChapter
    case _ => throw new IndexOutOfBoundsException(i)
  }

  private val onlyChapter = new OneShotChapter(story, browser, page)
}

private class OneShotChapter(val story: Story, browser: Browser, page: Document) extends Chapter {
  override def chapterIndex: Int = 0
  override def postedOn: LocalDate = story.publishedOn

  override def paragraphs: Iterable[String] = page >> texts("div[role=article] p")


  override def title: String = navigationProvider.pollTitle(0)
  override def chapterId: Long = navigationProvider.pollChapterId(0)

  private val navigationProvider = new NavigationChapterInfoProvider(story, browser)
}
