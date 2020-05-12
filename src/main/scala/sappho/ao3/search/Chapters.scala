package sappho.ao3.search

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import sappho.ao3.{BaseChapters, Chapter}

private class Chapters(result: SearchResult) extends BaseChapters(result.li >> text("dd.chapters")) {
  override def apply(chapterIndex: Int): Chapter = result.page.chapters(chapterIndex)
}
