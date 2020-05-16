package sappho.ao3.search

import sappho.queries.Order

private object SearchUtils {
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
  private object AO3Order {
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
}
