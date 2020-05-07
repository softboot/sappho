package sappho

import java.net.URL

/** Represents a user of a fanfiction archive.
 *
 *  The Author trait provides methods for easy access to common author
 *  metadata (such as date of account creation or a bio). Implementations
 *  are encouraged to provide additional methods for archive-specific
 *  metadata, such as pseudonyms for Archive of Our Own.
 */
trait Author {
  /** Returns the author's username. */
  def name: String

  /** Returns the author's full name as seen in stories or comments.
   *
   *  What constitutes a full name is archive-dependent. In most cases, this name
   *  will be the same as the regular `name`; however, fanfiction archives which
   *  provide pseudonym functionality may define it differently. In all cases, if
   *  several forms of specifying the username are available, `fullName` is guaranteed
   *  to be the most information-rich one.
   */
  def fullName: String = name

  /** Returns the author's homepage URL.
   *
   *  An author's homepage is typically a hub which allows visitors to check out
   *  author metadata, as well as a list of stories they have contributed to.
   */
  def url: URL
}
