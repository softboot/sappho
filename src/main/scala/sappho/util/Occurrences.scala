package sappho.util

/** Provides utilities for analyzing distinct occurrences of elements. */
object Occurrences {
  implicit class DistinctCountingIterator[T](val iterator: Iterator[T]) extends AnyVal {
    /** Counts occurrences of all distinct values.
     *
     *  This method counts the occurrences of each distinct value returned
     *  by the iterator, producing an immutable map from the values to the
     *  total number of occurrences.
     *
     *  @return a map with distinct values as keys and the number of times
     *          they appeared in the iterator as values
     */
    def countDistinct(): Map[T, Int] = {
      iterator.foldLeft(Map.empty[T, Int])((map, e) => map.get(e) match {
        case Some(count) => map + (e -> (count + 1))
        case None => map + (e -> 1)
      })
    }
  }
}
