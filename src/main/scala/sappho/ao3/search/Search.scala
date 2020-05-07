package sappho.ao3.search

import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import net.ruippeixotog.scalascraper.browser.Browser
import sappho.queries.BooleanFilter
import sappho.queries.range.Range.NotEmpty
import sappho.queries.range._
import sappho.util.GetRequestBuilder

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Search {
  import Search._
  
  var primaryTag: String = ""
  val includedTags: mutable.Seq[String] = ArrayBuffer()
  val excludedTags: mutable.Seq[String] = ArrayBuffer()

  var crossoverFilter: BooleanFilter = BooleanFilter.Either
  var completeFilter: BooleanFilter = BooleanFilter.Either
  var wordCountRange: NotEmpty[Int] = Range[Int](Infinite, Infinite).asInstanceOf[NotEmpty[Int]]
  var revisionDateRange: NotEmpty[LocalDate] = Range[LocalDate](Infinite, Infinite).asInstanceOf[NotEmpty[LocalDate]]

  var ordering: String = "revised_at"
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
    
    builder.add("work_search[sort_column]", ordering)
      .add("work_search[other_tag_names]", includedTags.mkString(","))
      .add("work_search[excluded_tag_names]", excludedTags.mkString(","))
      .add("work_search[crossover]", filterToString(crossoverFilter))
      .add("work_search[complete]", filterToString(completeFilter))
      .add("work_search[words_from]", leftIntBoundToString(wordCountRange.lowerBound))
      .add("work_search[words_to]", rightIntBoundToString(wordCountRange.upperBound))
      .add("work_search[date_from]", leftDateBoundToString(revisionDateRange.lowerBound))
      .add("work_search[date_to]", rightDateBoundToString(revisionDateRange.upperBound))
      .addEmpty("work_search[query]")
      .add("work_search[language_id]", languageId)
      .add("tag_id", primaryTag, x => x) //TODO properly encode the primary tag

    new URL(builder.get)
  }
}

object Search {
  def baseUrl: URL = new URL("https://archiveofourown.org/works")
  
  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

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