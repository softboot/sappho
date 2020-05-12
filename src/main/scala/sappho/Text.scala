package sappho

import sappho.util.Words._

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

  /** Returns the text split into lexemes.
   *
   *  In the context of this library, a lexeme is any sequence of characters
   *  surrounded by whitespace, punctuation, or paragraph boundaries.
   *  Hyphens and apostrophes within a lexeme are allowed (for instance,
   *  "well-read" and "let's" both constitute a single lexeme); however,
   *  trailing hyphens and apostrophes are treated as punctuation.
   *
   *  Under most circumstances, lexemes represent single words or numbers,
   *  although sequences of special symbols (such as mathematical notation
   *  or emoji) may be matched as well. This behavior may change in a future
   *  version.
   */
  def lexemes: Iterable[String] = paragraphs.flatMap(_.splitIntoLexemes())

  /** Returns the text split into words. */
  def words: Iterable[String] = paragraphs.flatMap(_.splitIntoWords())
}
