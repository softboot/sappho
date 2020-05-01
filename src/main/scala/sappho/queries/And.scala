package sappho.queries
import sappho.Story

class And private(private val conditionMap: Map[Criterion[Any], Condition]) extends Clause {
  override def apply(story: Story): Boolean = conditionMap.values.forall(_(story))

  override def criteria: Set[Criterion[Any]] = conditionMap.keySet

  override def conditionFor(criterion: Criterion[Any]): Option[Condition] = conditionMap.get(criterion)

  override def and(other: Clause): Clause = other match {
    case True => this
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
          case True => new And(conditionMap - c.criterion).normalized
          case newCondition: Condition => new And(conditionMap + (c.criterion -> newCondition))
        }
    }
    else {
      new And(conditionMap + (c.criterion -> c))
    }
  }

  override def tryOr(other: Clause): Option[Clause] = other match {
    case True | False => other.tryOr(this)
    case condition: Condition => Some(condition).filter(conditionMap.values.iterator.contains)
    case that: And =>
      val leftConditions = this.conditionMap.values.toSet[Condition]
      val rightConditions = that.conditionMap.values.toSet[Condition]

      val leftUnique = leftConditions -- rightConditions
      val rightUnique = rightConditions -- leftConditions

      if(leftUnique.size == 1 && rightUnique.size == 1 && leftUnique.head.criterion == rightUnique.head.criterion) {
        val commonCriterion = leftUnique.head.criterion

        (leftUnique.head tryOr rightUnique.head).map(_ match {
          case False => False
          case True => new And(conditionMap - commonCriterion).normalized
          case newCondition: Condition => new And(conditionMap + (commonCriterion -> newCondition))
        })
      }
      else if(leftUnique.isEmpty) Some(this)
      else if(rightUnique.isEmpty) Some(that)
      else None
  }

  override def not(): Query = conditionMap.values
    .map(_.not())
    .foldLeft(False.asInstanceOf[Query])(_ or _)

  override def normalized: Clause = conditionMap.size match {
    case 0 => False
    case 1 => conditionMap.values.head
    case _ => this
  }

  override def toString(): String = conditionMap.values.mkString("(", " && ", ")")

  override def equals(other: Any): Boolean = other match {
    case that: And => this.conditionMap == that.conditionMap
    case _ => false
  }

  override def hashCode(): Int = conditionMap.hashCode
}
object And {
  def apply(conditions: Condition*): Clause = conditions
    .foldLeft(new And(Map.empty).asInstanceOf[Clause])(_ and _)
    .normalized

  def unapply(and: And): Option[Iterable[Condition]] = Some(and.conditionMap.values)
}