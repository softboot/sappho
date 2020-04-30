package sappho.queries

import sappho.Story
import sappho.queries.BooleanFilter._

class BooleanCondition private(val criterion: Criterion, predicate: Story => Boolean, val filter: BooleanFilter)
  extends Condition
{
  override def apply(story: Story): Boolean = filter.satisfiedBy(predicate(story))

  override def and(other: Query): Query = other match {
    case clause: Clause => this and clause
  }

  override def and(other: Clause): Clause = other match {
    case True => this
    case False => False
    case anAnd: And => anAnd.and(this)
    case similar: BooleanCondition if similar.criterion == this.criterion =>
      BooleanCondition(criterion, predicate)(this.filter and similar.filter)
    case different: Condition => And(this, different)
  }

  override def tryOr(other: Clause): Option[Clause] = other match {
    case True | False | And(_) => other.tryOr(this)
    case similar: BooleanCondition if similar.criterion == this.criterion =>
      Some(BooleanCondition(criterion, predicate)(this.filter or similar.filter))
    case _: Condition => None
  }

  override def toString(): String = {
    filter match {
      case Set => criterion.name
      case Unset => "!" + criterion.name
      case Either => "tautology[" + criterion.name + "]"
      case Neither => "contradiction[" + criterion.name + "]"
    }
  }

  override def equals(other: Any): Boolean = other match {
    case that: BooleanCondition => this.criterion == that.criterion && this.filter == that.filter
    case _ => false
  }

  override def hashCode(): Int = {
    31 * criterion.hashCode + filter.hashCode
  }
}

object BooleanCondition {
  def apply(criterion: Criterion, predicate: Story => Boolean)(filter: BooleanFilter = Set): Clause = {
    filter match {
      case Neither => False
      case Either => True
      case _ => new BooleanCondition(criterion, predicate, filter)
    }
  }

  val Complete = BooleanCondition(Criterion("complete"), _.isComplete)()
  val OneShot = BooleanCondition(Criterion("oneShot"), _.isOneShot)()
}