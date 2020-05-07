package sappho

import java.net.URL

import net.ruippeixotog.scalascraper.browser.Browser

/** Represents a fanfiction archive.
 *
 *  Implementations of this trait represent a single fanfiction archive, such as fanfiction.net
 *  or Archive of Our Own.
 *
 *  This trait defines a set of methods describing common tasks, such as fetching stories
 *  or executing queries, which allows for a generalized, archive-agnostic scraping in basic
 *  use cases. Implementations are encouraged to provide additional methods for archive-specific
 *  data retrieval where applicable.
 *
 *  Note that since implementations of this trait typically serve as stateless APIs, they should
 *  usually be implemented as singleton objects.
 *
 *  @see sappho.ao3.Archive
 */
trait Archive {
  /** Returns the full name of the archive. */
  def name: String

  /** Returns an abbreviation of the archive's name. */
  def abbreviation: String

  /** Returns the archive's homepage address. */
  def homepage: URL

  /** Fetches a story by its id. */
  def fetchStoryById(storyId: Long)(implicit browser: Browser): Story
}
