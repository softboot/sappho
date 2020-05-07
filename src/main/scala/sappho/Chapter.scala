package sappho

import java.net.URL
import java.time.LocalDate

/** Represents a single chapter of a story.
 *
 *  The Chapter trait defines methods providing access to common chapter
 *  metadata (such as the title or the date a chapter was posted).
 *  Implementations are encouraged to expand on the available set of methods
 *  to provide access to archive-specific metadata.
 *
 *  This trait also extends the Text base trait, granting access to
 *  the chapter's content. Note that while retrieving the full text of
 *  a chapter in the form of a single `String` is allowed, iterating
 *  through the paragraphs is usually preferable.
 */
trait Chapter extends Text {
  /** Returns the archive this chapter was published to. */
  def archive: Archive

  /** Returns the story this chapter is a part of. */
  def story: Story


  /** Returns the chapter id.
   *
   *  The chapter id usually constitutes a part of the chapter's URL.
   *  Its exact form is archive-dependent; in particular, it is not guaranteed
   *  to be unique, as chapters of different stories may share the same
   *  chapter id. Therefore, a chapter id is useless without knowing
   *  at least the archive and story id.
   */
  def chapterId: Long

  /** Returns the chapter index.
   *
   *  The chapter index describes a chapter's place in the story, counting from 0.
   *  For instance, the first chapter of the story has an index of 0, the second chapter has
   *  an index of 1, etc.
   */
  def chapterIndex: Int

  /** Returns the chapter's URL. */
  def url: URL


  /** Returns the title of this chapter. */
  def title: String

  /** Returns the date on which the chapter was posted.
   *
   *  Note that for some fanfiction archives, this is a potentially expensive operation,
   *  as the dates on which chapters were published may be listed on a different page.
   */
  def postedOn: LocalDate
}
