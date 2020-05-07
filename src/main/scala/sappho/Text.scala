package sappho

/** Defines text access methods common to stories and chapters.
 *
 *  This trait allows the users to traverse an implementation-defined
 *  block of text divided into paragraphs or retrieve it in the form
 *  of a single `String` (discouraged).
 */
trait Text {
  /** Returns a lazy iterable of all paragraphs of the text. */
  def paragraphs: Iterable[String]

  /** Returns the entire text. */
  def fullText: String = paragraphs.mkString("\n\n")
}
