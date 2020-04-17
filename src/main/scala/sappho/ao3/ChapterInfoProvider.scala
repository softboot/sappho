package sappho.ao3

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private trait ChapterInfoProvider {
  def pollChapterId(chapterIndex: Int): Long
  def pollTitle(chapterIndex: Int): String
}

private class DropdownListChapterInfoProvider(page: Document) extends ChapterInfoProvider {
  private lazy val list = (page >> element("#selected_id")).children.toIndexedSeq

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