package sappho

trait Chapters extends IndexedSeq[Chapter] {
  def count: Int
  def plannedCount: Option[Int]

  def apply(i: Int): Chapter

  override def iterator: Iterator[Chapter] = (0 until count).iterator.map(apply)

  final override def length: Int = count
}
