package sappho.ao3.search

import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import net.ruippeixotog.scalascraper.browser.Browser
import sappho.queries.range._
import sappho.queries.{BooleanFilter, Order}
import sappho.util.GetRequestBuilder

import scala.collection.mutable

private class Search {
  import Search._

  def primaryTag: String = includedTags
    .headOption
    .getOrElse(throw new IllegalArgumentException("At least one included tag must be provided"))
  
  val includedTags: mutable.Set[String] = mutable.HashSet()
  val excludedTags: mutable.Set[String] = mutable.HashSet()

  private var ordering0: Order[Any] = Order.byDateOfUpdate
  private var crossoverFilter0: BooleanFilter = BooleanFilter.Either
  private var completeFilter0: BooleanFilter = BooleanFilter.Either
  private var wordCountRange0: Range.NotEmpty[Int] = Range.Unbounded[Int]()
  private var revisionDateRange0: Range.NotEmpty[LocalDate] = Range.Unbounded[LocalDate]()

  def crossoverFilter = crossoverFilter0
  def crossoverFilter_=(filter: BooleanFilter): Unit = {
    if(filter == BooleanFilter.Neither)
      throw new IllegalArgumentException("Self-contradictory query: crossover filter set to Neither")
    crossoverFilter0 = filter
  }

  def completeFilter = completeFilter0
  def completeFilter_=(filter: BooleanFilter): Unit = {
    if(filter == BooleanFilter.Neither)
      throw new IllegalArgumentException("Self-contradictory query: complete filter set to Neither")
    completeFilter0 = filter
  }

  def wordCountRange: Range[Int] = wordCountRange0
  def wordCountRange_=(range: Range[Int]): Unit = {
    if(range.isEmpty)
      throw new IllegalArgumentException("Self-contradictory query: empty word count range")
    wordCountRange0 = range.notEmpty
  }

  def revisionDateRange: Range[LocalDate] = revisionDateRange0
  def revisionDateRange_=(range: Range[LocalDate]): Unit = {
    if(range.isEmpty)
      throw new IllegalArgumentException("Self-contradictory query: empty word count range")
    revisionDateRange0 = range.notEmpty
  }

  def ordering = ordering0
  def ordering_=(order: Order[Any]): Unit = {
    order match {
      case Order.byDateOfUpdate => ordering0 = order
      case _ => throw new IllegalArgumentException(s"${order} not supported by AO3")
    }
  }

  var languageId: String = ""


  def page(pageIndex: Int)(implicit browser: Browser): ResultPage = {
    val url = urlForPage(pageIndex)
    new ResultPage(url, browser)
  }

  def pages()(implicit browser: Browser): Iterable[ResultPage] = {
    val firstPage = page(0)
    Iterator.iterate[Option[ResultPage]](Some(firstPage))(p => p.get.nextPage)
      .takeWhile(_.isDefined)
      .map(_.get)
      .to(Iterable)
  }

  def results()(implicit browser: Browser): Iterable[SearchResult] = pages.flatten


  private def urlForPage(pageIndex: Int = 0): URL = {
    val builder = new GetRequestBuilder(baseUrl)
      .add("utf8", "\u2713")
      .add("commit", "Sort and Filter")
    
    if(pageIndex > 0)
      builder.add("page", (pageIndex + 1).toString)

    val primary = primaryTag
    
    builder.add("work_search[sort_column]", orderToString(ordering))
      .add("work_search[other_tag_names]", (includedTags - primary).mkString(","))
      .add("work_search[excluded_tag_names]", excludedTags.mkString(","))
      .add("work_search[crossover]", filterToString(crossoverFilter))
      .add("work_search[complete]", filterToString(completeFilter))
      .add("work_search[words_from]", leftIntBoundToString(wordCountRange0.lowerBound))
      .add("work_search[words_to]", rightIntBoundToString(wordCountRange0.upperBound))
      .add("work_search[date_from]", leftDateBoundToString(revisionDateRange0.lowerBound))
      .add("work_search[date_to]", rightDateBoundToString(revisionDateRange0.upperBound))
      .addEmpty("work_search[query]")
      .add("work_search[language_id]", languageId)
      .add("tag_id", primary, x => x) //TODO properly encode the primary tag

    new URL(builder.get)
  }
}

object Search {
  def baseUrl: URL = new URL("https://archiveofourown.org/works")
  
  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  private def orderToString(order: Order[Any]): String = order match {
    case Order.byDateOfUpdate => "revised_at"
    case _ =>
      //Unsupported order should have been detected much sooner.
      throw new IllegalStateException("Invalid order encountered")
  }

  private def filterToString(filter: BooleanFilter): String = filter match {
    case BooleanFilter.Set => "T"
    case BooleanFilter.Unset => "F"
    case BooleanFilter.Either => ""
    case BooleanFilter.Neither =>
      //A Neither flag should have been detected much sooner.
      throw new IllegalStateException("Invalid boolean flag encountered: Neither")
  }

  private def leftIntBoundToString(bound: Bound[Int]): String = bound match {
    case Infinite => ""
    case Inclusive(e) => e.toString
    case Exclusive(e) => (e + 1).toString
  }

  private def leftDateBoundToString(bound: Bound[LocalDate]): String = bound match {
    case Infinite => ""
    case Inclusive(date) => dateFormatter.format(date)
    case Exclusive(date) => dateFormatter.format(date.plusDays(1))
  }

  private def rightIntBoundToString(bound: Bound[Int]): String = bound match {
    case Infinite => ""
    case Inclusive(e) => e.toString
    case Exclusive(e) => (e - 1).toString
  }

  private def rightDateBoundToString(bound: Bound[LocalDate]): String = bound match {
    case Infinite => ""
    case Inclusive(date) => dateFormatter.format(date)
    case Exclusive(date) => dateFormatter.format(date.minusDays(1))
  }
}