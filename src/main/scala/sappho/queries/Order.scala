package sappho.queries

import java.time.LocalDate

import sappho.Story

/** Represents the ordering of search results.
 *
 *  Instances of this trait represent the requested order of stories in search results.
 *  When a query is executed, the requested `Order` is passed as one of the parameters
 *  to the fanfiction archive's search engine. In addition, if a query requires several
 *  different searches to be made (for instance, if the search engine only supports AND
 *  semantics and the query specifies several possibilities with OR), the ordering is
 *  used to merge results into a single `Iterable`.
 *
 *  The provided orderings include sorting based on criteria such as title, date of publishing,
 *  date of last update, word count, view count, or score. They are defined in the `Order`
 *  companion object.
 *
 *  Note that not all possible orderings are supported by every fanfiction archive.
 *  If a fanfiction archive does not support the requested ordering, an `UnsupportedOrderException`
 *  is thrown. If your use case requires the stories to be sorted in an order unsupported
 *  by the fanfiction archive or based on a criterion not listed in this documentation,
 *  consider collecting all search results into a list and then applying the required ordering
 *  to that list on client side.
 */
sealed trait Order[+T] extends Ordering[Story] {
  /** Returns the name of this order (used for debugging and error messages). */
  def name: String


  /** Checks if this order is ascending.
   *
   *  For the purposes of our library, an order is considered ascending if and only if
   *  a story with the criterion which is less than the corresponding criterion of another story
   *  compares as less than that other story.
   */
  def isAscending: Boolean

  /** Checks if this order is descending.
   *
   *  For the purposes of our library, an order is considered descending if and only if
   *  a story with the criterion which is less than the corresponding criterion of another story
   *  compares as greater than that other story.
   */
  def isDescending: Boolean = !isAscending


  /** Returns the corresponding ascending order.
   *
   *  If this order is already ascending, return `this`. Otherwise, return an ascending order
   *  based on the same criterion.
   */
  def ascending: Order.Ascending[T]

  /** Returns the corresponding descending order.
   *
   *  If this order is already descending, return `this`. Otherwise, return a descending order
   *  based on the same criterion.
   */
  def descending: Order.Descending[T]


  /** Compare two stories based on the requested criterion.
   *
   *  For the purposes of this trait, a story is considered less than another story
   *  if and only if it should appear earlier in the search results.
   */
  def compare(x: Story, y: Story): Int

  /** Returns an order opposite to the current one.
   *
   *  If this order is ascending, returns a descending order based on the same criterion.
   *  If this order is descending, returns an ascending order based on the same criterion.*/
  def reverse: Ordering[Story]
}

object Order {
  final class Ascending[+T](val name: String, extractor: Story => T)(implicit ordering: Ordering[T]) extends Order[T] {
    private val ord: Ordering[Story] = Ordering.by(extractor)

    override def isAscending = true
    override def ascending: Ascending[T] = this
    override val descending: Descending[T] = new Descending(this)

    override def compare(x: Story, y: Story) = ord.compare(x, y)
    override def reverse: Descending[T] = descending


    override def hashCode: Int = 2 * name.hashCode

    override def equals(other: Any): Boolean = other match {
      case that: Ascending[T] => this.name == that.name
      case _ => false
    }

    override def toString: String = s"Order.Ascending($name)"
  }

  final class Descending[+T](val ascending: Ascending[T]) extends Order[T] {
    override def name: String = ascending.name

    override def isAscending = false
    override def descending = this

    override def compare(x: Story, y: Story) = ascending.compare(y, x)
    override def reverse: Ascending[T] = ascending


    override def hashCode: Int = ascending.hashCode + 1

    override def equals(other: Any): Boolean = other match {
      case that: Descending[T] => this.name == that.name
      case _ => false
    }

    override def toString: String = s"Order.Descending($name)"
  }


  /** Returns an ascending order based on the provided property of a story. */
  def ascending[T](name: String, extractor: Story => T)(implicit ordering: Ordering[T]) = new Ascending(name, extractor)

  /** Returns a descending order based on the provided property of a story. */
  def descending[T](name: String, extractor: Story => T)(implicit ordering: Ordering[T]) = ascending(name, extractor).reverse


  /** Order by title.
   *
   *  By default, titles are sorted alphabetically. You can specify the direction of the ordering
   *  explicitly by calling the `ascending` or `descending` methods.
   */
  val byTitle: Order[String] = ascending("title", _.title)

  /** Order by date of publishing.
   *
   *  By default, stories which were published later are displayed first. You can specify
   *  the direction of the ordering explicitly by calling the `ascending` or `descending`
   *  methods.
   */
  val byDateOfPublishing: Order[LocalDate] = descending("publishedOn", _.publishedOn)

  /** Order by date of last update.
   *
   *  By default, stories which were updated later are displayed first. You can specify
   *  the direction of the ordering explicitly by calling the `ascending` or `descending`
   *  methods.
   */
  val byDateOfUpdate: Order[LocalDate] = descending("updatedOn", _.updatedOn)

  /** Order by word count.
   *
   *  By default, stories with the highest word count are displayed first. You can specify
   *  the direction of the ordering explicitly by calling the `ascending` or `descending`
   *  methods.
   */
  val byWordCount: Order[Int] = descending("wordCount", _.wordCount)

  /** Order by number of views.
   *
   *  By default, stories with the most views are displayed first. You can specify
   *  the direction of the ordering explicitly by calling the `ascending` or `descending`
   *  methods.
   */
  val byViewCount: Order[Int] = descending("views", _.views)

  /** Order by score.
   *
   *  By default, stories with the highest score are displayed first. You can specify
   *  the direction of the ordering explicitly by calling the `ascending` or `descending`
   *  methods.
   */
  val byScore: Order[Int] = descending("score", _.score)
}