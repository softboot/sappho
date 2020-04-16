package sappho

trait Chapters extends (Int => Chapter) {
  def count: Int
  def plannedCount: Option[Int]

  def apply(i: Int): Chapter
}
