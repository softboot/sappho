package sappho

import java.net.URL
import java.time.LocalDate

import sappho.tags.Tags

/** Represents a single story published on a fanfiction archive.
 *
 *  The Story trait defines methods providing access to common
 *  story metadata, such as the title and author(s), the date
 *  of publishing, or word count. Implementations are encouraged
 *  to expand on the existing methods to provide access to
 *  archive-specific story metadata.
 *
 *  The content of the story can be explored on a chapter-by-chapter
 *  basis through the use of the `chapters` property. Note that by
 *  default, chapter pages are loaded on access; if you wish to
 *  load all chapters in advance or refer to chapters multiple times
 *  without storing them in an external collection, consider using
 *  `fullWork.chapters` instead.
 *
 *  The content of the story can also be digested in the form of
 *  a single lazily-evaluated stream of paragraphs. Again, by default,
 *  chapter pages are only loaded on access, so if you stop iteration
 *  partway through, only the chapters you visited will have been
 *  downloaded. This is generally a good thing, but if you wish to load
 *  all chapters in advance, you may opt to use `fullWork.paragraphs`
 *  instead.
 *
 *  Lastly, the content of the story can also be fetched in the form of a
 *  single `String`. As many stories are hundreds of thousands of words long,
 *  this is not recommended.
 */
trait Story extends Text with Tags {
  /** Returns the archive this story was published to. */
  def archive: Archive

  /** Returns the story id.
   *
   *  The story id usually constitutes a part of the story's URL.
   *  Its exact form is archive-dependent, but it is always guaranteed
   *  to be unique within a given archive.
   *  */
  def storyId: Long

  /** Returns the story's URL. */
  def url: URL


  /** Returns the title of the story. */
  def title: String

  /** Returns the list of authors of this story.
   *
   *  In most cases, the sequence will be exactly one element long. However,
   *  for archives which support collaboration between authors, this sequence
   *  can be longer.
   */
  def authors: Seq[Author]

  /** Returns the language this story was written in.
   *
   *  The exact form of language information is archive-dependant.
   */
  def language: String


  /** Returns the date on which this story was published. */
  def publishedOn: LocalDate

  /** Returns the date on which this story was last updated. */
  def updatedOn: LocalDate

  /** Returns the date on which this story was completed, if applicable.
   *
   *  If the story is complete, returns `Some(updatedOn)`; otherwise,
   *  returns `None`.
   */
  def completedOn: Option[LocalDate]


  /** Returns the total word count for this story. */
  def wordCount: Int

  /** Returns the total score for this story.
   *
   *  The exact meaning of this value is archive-dependent; however,
   *  it is always a measure of audience appreciation. It may represent
   *  the number of "likes", "faves", or "kudos" a story has received,
   *  or otherwise a difference between "upvotes" and "downvotes".
   */
  def score: Int

  /** Returns the view count for this story. */
  def views: Int


  /** Checks if the story is complete; that is, if no more chapters are planned. */
  def isComplete: Boolean

  /** Checks if the story is a one-shot.
   *
   *  A one-shot is defined as a single-chapter complete story.
   */
  def isOneShot: Boolean


  /** Returns an object representing the chapters of the story.
   *
   *  For more information, see `sappho.Chapters`.
   *
   *  @see sappho.Chapters
   */
  def chapters: Chapters

  override def paragraphs: Iterable[String] = chapters.iterator
    .flatMap(ch => ch.paragraphs)
    .to(Iterable)
}
