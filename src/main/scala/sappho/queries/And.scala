package sappho.queries
import sappho.Story

class And private(private val conditionMap: Map[Criterion, Condition]) extends Clause {
  override def apply(story: Story): Boolean = conditionMap.values.forall(_(story))

  override def conditionFor(criterion: Criterion): Option[Condition] = conditionMap.get(criterion)

  override def and(other: Query): Query = other match {
    case clause: Clause => this and clause
    case _ => ???
  }

  override def and(other: Clause): Clause = other match {
    case False => False
    case anAnd: And => this andAllConditions anAnd
    case aCondition: Condition => this andCondition aCondition
  }

  private def andAllConditions(other: And): Clause = other.conditionMap.values
    .foldLeft(this.asInstanceOf[Clause])(_ and _)

  private def andCondition(c: Condition): Clause = {
    if(conditionMap contains c.criterion) {
        conditionMap(c.criterion).and(c) match {
          case False => False
          case newCondition: Condition => new And(conditionMap + (c.criterion -> newCondition))
        }
    }
    else {
      new And(conditionMap + (c.criterion -> c))
    }
  }

  override def toString(): String = conditionMap.values.mkString("(", " && ", ")")
}
object And {
  def apply(conditions: Condition*): Clause = conditions
    .foldLeft(new And(Map.empty).asInstanceOf[Clause])(_ and _)

  def unapply(and: And): Iterable[Condition] = and.conditionMap.values
}