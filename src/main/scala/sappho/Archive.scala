package sappho

import java.net.URL

import net.ruippeixotog.scalascraper.browser.Browser
import sappho.queries.{Order, Query}

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

  /** Fetches stories matching a specified query.
   *
   *  This method executes a given query, returning a lazily-evaluated sequence of stories
   *  fulfilling the required conditions, sorted in the defined order.
   */
  def search(query: Query, order: Order[Any])(implicit browser: Browser): IterableOnce[Story]

  /** Checks if the given order is supported by the archive.
   *
   *  Because stories are sorted on server side, only orderings supported by the archive's
   *  search engine can be used.
   */
  def supportsOrder(order: Order[Any]): Boolean
}
