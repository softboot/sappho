package sappho.ao3.search

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import sappho.queries.range.{Bound, Exclusive, Inclusive, Infinite}
import sappho.queries.{BooleanFilter, Order}

private[ao3] object SearchUtils {
  val queryDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  implicit class AO3Order(val order: Order[Any]) extends AnyVal {
    import AO3Order._

    def supportedByAO3: Order[Any] = toOrderStringOption
      .map(_ => order)
      .getOrElse(throw new IllegalArgumentException(s"$order not supported by AO3"))

    def isSupportedByAO3: Boolean = toOrderStringOption.isDefined

    def toOrderString: String = toOrderStringOption match {
      case Some(string) => string
      case None => throw new IllegalArgumentException(s"$order not supported by AO3")
    }

    def toOrderStringOption: Option[String] = matchOrderToString(order.ascending)
      .orElse(matchOrderToString(order.descending))

    def toDirectionString: String = if(order.isAscending) "asc" else "desc"
  }
  object AO3Order {
    private def matchOrderToString(order: Order[Any]): Option[String] = order match {
      case Order.byTitle => Some("title_to_sort_on")
      case Order.byDateOfPublishing => Some("created_at")
      case Order.byDateOfUpdate  => Some("revised_at")
      case Order.byWordCount => Some("word_count")
      case Order.byViewCount => Some("hits")
      case Order.byScore => Some("kudos_count")
      case _ => None
    }
  }


  implicit class AO3Filter(val filter: BooleanFilter) extends AnyVal {
    def toFilterString: String = filter match {
      case BooleanFilter.Set => "T"
      case BooleanFilter.Unset => "F"
      case BooleanFilter.Either => ""
      case BooleanFilter.Neither =>
        throw new IllegalArgumentException("Boolean filter 'Neither' should not appear in a clause")
    }
  }


  implicit class AO3IntBound(val bound: Bound[Int]) extends AnyVal {
    def toLeftBoundString: String = bound match {
      case Infinite => ""
      case Inclusive(e) => e.toString
      case Exclusive(e) => (e + 1).toString
    }

    def toRightBoundString: String = bound match {
      case Infinite => ""
      case Inclusive(e) => e.toString
      case Exclusive(e) => (e - 1).toString
    }
  }


  implicit class AO3DateBound(val bound: Bound[LocalDate]) extends AnyVal {
    def toLeftBoundString: String = bound match {
      case Infinite => ""
      case Inclusive(date) => queryDateFormatter.format(date)
      case Exclusive(date) => queryDateFormatter.format(date.plusDays(1))
    }

    def toRightBoundString: String = bound match {
      case Infinite => ""
      case Inclusive(date) => queryDateFormatter.format(date)
      case Exclusive(date) => queryDateFormatter.format(date.minusDays(1))
    }
  }
}
