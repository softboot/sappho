package sappho.ao3.search

import java.net.URL
import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.Browser
import sappho.ao3.search.SearchUtils._
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
  def ordering_=(order: Order[Any]): Unit = ordering0 = order.supportedByAO3

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
    
    builder.add("work_search[sort_column]", ordering.toOrderString)
      .add("work_search[sort_direction]", ordering.toDirectionString)
      .add("work_search[other_tag_names]", (includedTags.toSet - primary).mkString(","))
      .add("work_search[excluded_tag_names]", excludedTags.mkString(","))
      .add("work_search[crossover]", crossoverFilter.toFilterString)
      .add("work_search[complete]", completeFilter.toFilterString)
      .add("work_search[words_from]", wordCountRange0.lowerBound.toLeftBoundString)
      .add("work_search[words_to]", wordCountRange0.upperBound.toRightBoundString)
      .add("work_search[date_from]", revisionDateRange0.lowerBound.toLeftBoundString)
      .add("work_search[date_to]", revisionDateRange0.upperBound.toRightBoundString)
      .addEmpty("work_search[query]")
      .add("work_search[language_id]", languageId)
      .add("tag_id", primary)

    new URL(builder.get)
  }
}

object Search {
  def baseUrl: URL = new URL("https://archiveofourown.org/works")
}