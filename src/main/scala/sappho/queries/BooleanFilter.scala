package sappho.queries

sealed abstract class BooleanFilter {
  def satisfiedBy(boolean: => Boolean): Boolean
  def satisfiedByOption(booleanOption: => Option[Boolean]): Boolean
  def and(other: BooleanFilter): BooleanFilter
  def or(other: BooleanFilter): BooleanFilter
  def not(): BooleanFilter
}
object BooleanFilter {
  case object Set extends BooleanFilter {
    override def satisfiedBy(boolean: => Boolean) = boolean

    override def satisfiedByOption(booleanOption: => Option[Boolean]) = booleanOption.contains(true)

    override def and(other: BooleanFilter) = other match {
      case Set | Either => Set
      case _ => Neither
    }

    override def or(other: BooleanFilter) = other match {
      case Unset | Either => Either
      case _ => Set
    }

    override def not() = Unset

    override def toString = "Set"
  }

  case object Unset extends BooleanFilter {
    override def satisfiedBy(boolean: => Boolean) = !boolean

    override def satisfiedByOption(booleanOption: => Option[Boolean]) = booleanOption.contains(false)

    override def and(other: BooleanFilter) = other match {
      case Unset | Either => Unset
      case _ => Neither
    }

    override def or(other: BooleanFilter) = other match {
      case Set | Either => Either
      case _ => Unset
    }

    override def not() = Set

    override def toString = "Unset"
  }

  case object Either extends BooleanFilter {
    override def satisfiedBy(boolean: => Boolean) = true

    override def satisfiedByOption(booleanOption: => Option[Boolean]) = true

    override def and(other: BooleanFilter) = other

    override def or(other: BooleanFilter) = Either

    override def not() = Neither

    override def toString = "Either"
  }

  case object Neither extends BooleanFilter {
    override def satisfiedBy(boolean: => Boolean) = false

    override def satisfiedByOption(booleanOption: => Option[Boolean]) = false

    override def and(other: BooleanFilter) = Neither

    override def or(other: BooleanFilter) = other

    override def not() = Either

    override def toString = "Neither"
  }
}