package sappho.queries

import java.time.LocalDate

import sappho.Story
import sappho.queries.range.Range.Empty
import sappho.queries.range.{Infinite, Range};

class RangeCondition[T] private(val criterion: Criterion[T], val range: Range[T])
  extends Condition
{
  override def apply(story: Story): Boolean = {
    val comparedValue = criterion(story)
    comparedValue.exists(range.contains)
  }

  override def and(clause: Clause): Clause = clause match {
    case True => this
    case False => False
    case anAnd: And => anAnd.and(this)
    case similar: RangeCondition[T] if similar.criterion == this.criterion =>
      RangeCondition(criterion)(this.range intersect similar.range)
    case different: Condition => And(this, different)
  }

  override def tryOr(other: Clause): Option[Clause] = other match {
    case True | False | And(_) => other.tryOr(this)
    case similar: RangeCondition[T] if similar.criterion == this.criterion =>
      (this.range union similar.range)
          .map(newRange => RangeCondition(criterion)(newRange))
    case _: Condition => None
  }

  override def not(): Query = range.complement()
    .map(RangeCondition(criterion)(_))
    .foldLeft(False.asInstanceOf[Query])(_ or _)

  override def normalized = range match {
    case Empty() => False
    case Range(Infinite, Infinite) => True
    case _ => this
  }

  override def toString(): String = range.toConditionString(criterion.name)

  override def equals(other: Any): Boolean = other match {
    case that: RangeCondition[T] => this.criterion == that.criterion && this.range == that.range
    case _ => false
  }

  override def hashCode(): Int = {
    31 * criterion.hashCode + range.hashCode
  }
}

object RangeCondition {
  def apply[T](criterion: Criterion[T])(range: Range[T]): Clause = {
    range match {
      case Empty() => False
      case Range(Infinite, Infinite) => True
      case _ => new RangeCondition[T](criterion, range)
    }
  }

  val WordCount = RangeCondition[Int](Criteria.WordCount) _
  val Score = RangeCondition[Int](Criteria.Score) _
  val Views = RangeCondition[Int](Criteria.Views) _

  val ChapterCount = RangeCondition[Int](Criteria.ChapterCount) _
  val PlannedChapterCount = RangeCondition[Int](Criteria.PlannedChapterCount) _

  val PublishedOn = RangeCondition[LocalDate](Criteria.PublishedOn) _
  val UpdatedOn = RangeCondition[LocalDate](Criteria.UpdatedOn) _
}