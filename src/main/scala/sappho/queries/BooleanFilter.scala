package sappho.queries

sealed abstract class BooleanFilter {
  def satisfiedBy(boolean: => Boolean): Boolean
  def and(other: BooleanFilter): BooleanFilter
  def or(other: BooleanFilter): BooleanFilter
}
object BooleanFilter {
  case object Set extends BooleanFilter {
    override def satisfiedBy(boolean: => Boolean) = boolean

    override def and(other: BooleanFilter) = other match {
      case Set | Either => Set
      case _ => Neither
    }

    override def or(other: BooleanFilter) = other match {
      case Unset | Either => Either
      case _ => Set
    }

    override def toString = "Set"
  }

  case object Unset extends BooleanFilter {
    override def satisfiedBy(boolean: => Boolean) = !boolean

    override def and(other: BooleanFilter) = other match {
      case Unset | Either => Unset
      case _ => Neither
    }

    override def or(other: BooleanFilter) = other match {
      case Set | Either => Either
      case _ => Unset
    }

    override def toString = "Unset"
  }

  case object Either extends BooleanFilter {
    override def satisfiedBy(boolean: => Boolean) = true

    override def and(other: BooleanFilter) = other

    override def or(other: BooleanFilter) = Either

    override def toString = "Either"
  }

  case object Neither extends BooleanFilter {
    override def satisfiedBy(boolean: => Boolean) = false

    override def and(other: BooleanFilter) = Neither

    override def or(other: BooleanFilter) = other

    override def toString = "Neither"
  }
}