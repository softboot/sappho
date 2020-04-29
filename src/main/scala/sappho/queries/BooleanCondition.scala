package sappho.queries

import sappho.Story
import sappho.queries.BooleanFilter._

class BooleanCondition private(val criterion: Criterion, predicate: Story => Boolean, val filter: BooleanFilter)
  extends Condition
{
  override def apply(story: Story): Boolean = filter.satisfiedBy(predicate(story))

  override def and(other: Query): Query = other match {
    case True => this
    case clause: Clause => this and clause
    case _ => ???
  }

  override def and(other: Clause): Clause = other match {
    case False => False
    case anAnd: And => anAnd.and(this)
    case similar: BooleanCondition if similar.criterion == this.criterion =>
      this.filter and similar.filter match {
        case Neither => False
        //case Either => True
        case newFilter => new BooleanCondition(criterion, predicate, newFilter)
      }
    case different: Condition => And(this, different)
  }

  override def toString(): String = {
    filter match {
      case Set => criterion.name
      case Unset => "!" + criterion.name
      case Either => "tautology[" + criterion.name + "]"
      case Neither => "contradiction[" + criterion.name + "]"
    }
  }
}

object BooleanCondition {
  def apply(criterion: Criterion, predicate: Story => Boolean)(filter: BooleanFilter = Set): Clause = {
    filter match {
      case Neither => False
      case _ => new BooleanCondition(criterion, predicate, filter)
    }
  }

  val Complete = BooleanCondition(Criterion("complete"), _.isComplete)()
  val OneShot = BooleanCondition(Criterion("oneShot"), _.isOneShot)()
}