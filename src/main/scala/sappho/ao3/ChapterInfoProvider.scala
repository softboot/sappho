package sappho.ao3

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private trait ChapterInfoProvider {
  def pollChapterId(chapterIndex: Int): Long
  def pollTitle(chapterIndex: Int): String
}

private class DropdownListChapterInfoProvider(page: Document) extends ChapterInfoProvider {
  private val list = (page >> element("#selected_id")).children.toIndexedSeq

  override def pollChapterId(chapterIndex: Int): Long = {
    list(chapterIndex)
      .attr("value")
      .toInt
  }

  override def pollTitle(chapterIndex: Int): String = {
    val prefix = (chapterIndex + 1) + ". "
    val text = list(chapterIndex).text
    text.substring(prefix.length)
  }
}

private class NavigationChapterInfoProvider private(navigationPage: Document) extends ChapterInfoProvider {
  private val list = (navigationPage >> element("ol.chapter")).children.toIndexedSeq

  override def pollChapterId(chapterIndex: Int): Long = {
    val chapterUrl = (list(chapterIndex) >> element("a")).attr("href")
    val lastSeparatorIndex = chapterUrl.lastIndexOf('/')
    chapterUrl.substring(lastSeparatorIndex + 1).toLong
  }

  override def pollTitle(chapterIndex: Int): String = {
    val prefix = (chapterIndex + 1) + ". "
    val text = (list(chapterIndex) >> element("a")).text
    text.substring(prefix.length)
  }
}
private object NavigationChapterInfoProvider {
  def load(story: Story)(implicit browser: Browser): NavigationChapterInfoProvider = {
    val url = story.url.toString + "/navigate"
    val navigationPage = browser.get(url)
    makePreloaded(navigationPage)
  }

  def makePreloaded(navigationPage: Document): NavigationChapterInfoProvider = {
    new NavigationChapterInfoProvider(navigationPage)
  }
}