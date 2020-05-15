package sappho

/** Represents the collection of all chapters of a story.
 *
 *  Semantically, this trait behaves as a fixed-length sequence
 *  of chapters. This allows users of this trait to iterate
 *  through the chapters in a functional manner.
 *
 *  Note that by default, each element access causes a new page
 *  to be downloaded (with the possible exception of the first
 *  chapter, if it had already been fetched to scrape the story
 *  metadata). If for some reason you need to refer to chapters
 *  multiple times, consider calling `story.fullWork.chapters`
 *  instead of `story.chapters` to force all chapters to be downloaded
 *  just once, ahead of time.
 *
 *  This trait also grants access to the number of chapters currently
 *  published, as well as the total number of chapters planned,
 *  if such data is available.
 */
trait Chapters extends IndexedSeq[Chapter] {
  /** Returns the number of chapters currently published. */
  def count(): Int

  /** Returns the planned number of chapters, if such data is available.
   *
   *  If the number of chapters planned is known to be `x`, returns `Some(x)`;
   *  otherwise, returns `None`.
   */
  def plannedCount: Option[Int]

  /** Returns the current progress on the chapter, if such data is available.
   *
   *  We define the current progress on the chapter as the ratio of the current
   *  number of chapters to the total number of chapters planned, if it is known.
   *  If the planned number of chapters is unknown, `None` is returned.
   */
  def progress: Option[Double] = plannedCount.map(d => count().toDouble / d)


  /** Returns the i-th chapter in order, counting from 0. */
  def apply(i: Int): Chapter

  /** Returns a one-use iterator of all currently published chapters.  */
  override def iterator: Iterator[Chapter] = (0 until count()).iterator.map(apply)

  final override def length: Int = count()
}
