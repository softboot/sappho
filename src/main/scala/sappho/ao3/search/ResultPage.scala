package sappho.ao3.search

import java.net.URL

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.util.Log._

private class ResultPage(val url: URL, browser: Browser) extends Iterable[SearchResult] with StrictLogging {
  def prevPage: Option[ResultPage] = loadResultFromLink("li.previous a")

  def nextPage: Option[ResultPage] = loadResultFromLink("li.next a")

  override def iterator = (page >> elements("ol.work.index.group li.work.blurb.group"))
    .iterator
    .map(li => new SearchResult(li, browser))


  private val page: Document = browser.get(url, logger)

  private def loadResultFromLink(selector: String): Option[ResultPage] = (page >> elements(selector))
    .iterator
    .map(_ attr "href")
    .map("https://archiveofourown.org" + _)
    .map(new URL(_))
    .map(new ResultPage(_, browser))
    .nextOption
}
