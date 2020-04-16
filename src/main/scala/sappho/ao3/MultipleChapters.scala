package sappho.ao3

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private class MultipleChapters(page: Document) extends Chapters {
  private val progress = (page >> text("dd.chapters")).split("/")

  override def count: Int = progress(0).toInt
  override def plannedCount: Option[Int] = progress(1).toIntOption

  override def apply(i: Int): Chapter = ???
}
