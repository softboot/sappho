package sappho.ao3.search

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import sappho.ao3.Chapter

class Chapters(result: SearchResult) extends sappho.ao3.Chapters {
  private val progressText = (result.li >> text("dd.chapters")).trim.split("/")

  override def count: Int = progressText(0).toInt

  override def plannedCount: Option[Int] = progressText(1).toIntOption

  override def apply(chapterIndex: Int): Chapter = result.page.chapters(chapterIndex)
}
