package sappho.util

class MergingIterator[+T](iterables: Seq[IterableOnce[T]])(implicit ordering: Ordering[T]) extends Iterator[T] {
  private val iterators = iterables.map(_.iterator.buffered)

  override def hasNext = iterators.exists(_.hasNext)

  override def next() = iterators
    .filter(_.hasNext)
    .minByOption(_.head)
    .map(_.next())
    .getOrElse(throw new NoSuchElementException("Called next() on an empty MergingIterator"))
}
