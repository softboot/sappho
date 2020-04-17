package sappho.ao3

trait Chapters extends sappho.Chapters with IndexedSeq[Chapter]  {
  def apply(i: Int): Chapter

  override def iterator: Iterator[Chapter] = (0 until length).iterator.map(apply)
}
