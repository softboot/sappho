package sappho.ao3

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.util.Log._

private trait ChapterInfoProvider {
  def story: Story

  def pollChapterId(chapterIndex: Int): Long
  def pollTitle(chapterIndex: Int): String
  def pollDate(chapterIndex: Int): LocalDate
}

private class DropdownListChapterInfoProvider(val story: Story, browser: Browser, page: Document) extends ChapterInfoProvider {
  private val list = (page >> element("#selected_id")).children.toIndexedSeq
  private lazy val navigationProvider = new NavigationChapterInfoProvider(story, browser)

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

  override def pollDate(chapterIndex: Int): LocalDate = navigationProvider.pollDate(chapterIndex)
}

private class NavigationChapterInfoProvider(val story: Story, browser: Browser) extends ChapterInfoProvider with StrictLogging {
  import NavigationChapterInfoProvider._

  private val navigationPage = browser.get(story.url + "/navigate", logger)
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

  override def pollDate(chapterIndex: Int): LocalDate = {
    LocalDate.parse(list(chapterIndex) >> text(".datetime"), dateFormat)
  }
}
private object NavigationChapterInfoProvider {
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("(yyyy-MM-dd)")
}