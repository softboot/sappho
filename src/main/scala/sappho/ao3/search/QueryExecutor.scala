package sappho.ao3.search

import java.time.LocalDate

import net.ruippeixotog.scalascraper.browser.Browser
import sappho.ao3.Story
import sappho.queries._
import sappho.util.MergingIterator

object QueryExecutor {
  def executeQuery(query: Query, order: Order[Any])(implicit browser: Browser): Iterator[Story] = query match {
    case True => throw new IllegalArgumentException("Query too vague - you need to specify at least one tag")

    case False => Iterator.empty

    case Or(clauses) =>
      val individualSearches = clauses
        .map(clause => executeQuery(clause, order))
        .toIndexedSeq
      new MergingIterator[Story](individualSearches)(order.asInstanceOf[Ordering[Story]])

    case And(conditions) => executeConditions(conditions, order)

    case condition: Condition => executeConditions(Seq(condition), order)
  }

  def executeConditions(conditions: Iterable[Condition], order: Order[Any])(implicit browser: Browser): Iterator[Story] = {
    val search = new Search
    search.ordering = order
    for(condition <- conditions) {
      condition.criterion match {
        case Criteria.WordCount =>
          val range = condition.asInstanceOf[RangeCondition[Int]].range
          search.wordCountRange = range

        case Criteria.UpdatedOn =>
          val range = condition.asInstanceOf[RangeCondition[LocalDate]].range
          search.revisionDateRange = range

        case Criteria.Complete =>
          val filter = condition.asInstanceOf[BooleanCondition].filter
          search.completeFilter = filter

        case Criteria.Ratings | Criteria.Warnings | Criteria.Categories |
             Criteria.Fandoms | Criteria.Characters | Criteria.Relationships |
             Criteria.FreeformTags =>
          val tagCondition = condition.asInstanceOf[TagCondition]
          search.includedTags.addAll(tagCondition.includedTags)
          search.excludedTags.addAll(tagCondition.excludedTags)

        case _ => //Ignore.
      }
    }
    search.results().iterator
  }
}
