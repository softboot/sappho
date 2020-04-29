package sappho.queries

import sappho.Story
import sappho.queries.range.Range
import sappho.queries.range.Range.Empty;

class RangeCondition[T] private(val criterion: Criterion, extractor: Story => Option[T], val range: Range[T])
  extends Condition
{
  override def apply(story: Story): Boolean = {
    val comparedValue = extractor(story)
    comparedValue.exists(range.contains)
  }

  override def and(other: Query): Query = other match {
    case True => this
    case clause: Clause => this and clause
    case _ => ???
  }

  override def and(clause: Clause): Clause = clause match {
    case False => False
    case anAnd: And => anAnd.and(this)
    case similar: RangeCondition[T] if similar.criterion == this.criterion =>
      this.range intersect similar.range match {
        case Empty() => False
        case newRange => new RangeCondition[T](this.criterion, this.extractor, newRange)
      }
    case different: Condition => And(this, different)
  }

  override def toString(): String = range.toConditionString(criterion.name)
}

object RangeCondition {
  def apply[T](criterion: Criterion, extractor: Story => Option[T])(range: Range[T]): RangeCondition[T] = {
    new RangeCondition[T](criterion, extractor, range)
  }

  val WordCount = RangeCondition[Int](Criterion("wordCount"), story => Some(story.wordCount)) _
  val Score = RangeCondition[Int](Criterion("score"), story => Some(story.score)) _
  val Views = RangeCondition[Int](Criterion("views"), story => Some(story.views)) _

  val ChapterCount = RangeCondition[Int](Criterion("chapterCount"), story => Some(story.chapters.length)) _
  val PlannedChapterCount = RangeCondition[Int](Criterion("plannedChapterCount"), story => story.chapters.plannedCount) _
}