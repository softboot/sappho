package sappho.queries

import java.time.LocalDate

import sappho.queries.range.{Exclusive, FiniteRangeBuilder, Inclusive, RangeBuilder}

object Queries {
  abstract class LHS[T] {
    def leftLimit: T

    def <[R](rangeBuilder: RangeBuilder[T, R]): FiniteRangeBuilder[T, R] = {
      new FiniteRangeBuilder[T, R](Exclusive(leftLimit), rangeBuilder)
    }
    def <=[R](rangeBuilder: RangeBuilder[T, R]): FiniteRangeBuilder[T, R] = {
      new FiniteRangeBuilder[T, R](Inclusive(leftLimit), rangeBuilder)
    }
  }

  //For whatever reason, the < and <= operator overloads are not detected if LHS[Int] is used directly.
  //We need to override them with Int as a concrete, non-parametric type.
  implicit class IntLHS(val leftLimit: Int) extends LHS[Int] {
    override def <[R](rangeBuilder: RangeBuilder[Int, R]): FiniteRangeBuilder[Int, R] = super.<(rangeBuilder)
    override def <=[R](rangeBuilder: RangeBuilder[Int, R]): FiniteRangeBuilder[Int, R] = super.<=(rangeBuilder)
  }

  implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _

  implicit class LocalDateLHS(val leftLimit: LocalDate) extends LHS[LocalDate] {
    override def <[R](rangeBuilder: RangeBuilder[LocalDate, R]): FiniteRangeBuilder[LocalDate, R] = super.<(rangeBuilder)
    override def <=[R](rangeBuilder: RangeBuilder[LocalDate, R]): FiniteRangeBuilder[LocalDate, R] = super.<=(rangeBuilder)
  }


  val wordCount = RangeBuilder[Int, Query](RangeCondition.WordCount)
  val score = RangeBuilder[Int, Query](RangeCondition.Score)
  val views = RangeBuilder[Int, Query](RangeCondition.Views)

  val chapterCount = RangeBuilder[Int, Query](RangeCondition.ChapterCount)
  val plannedChapterCount = RangeBuilder[Int, Query](RangeCondition.PlannedChapterCount)

  val isComplete = BooleanCondition.Complete
  val isOneShot = BooleanCondition.OneShot

  val publishedOn = RangeBuilder[LocalDate, Query](RangeCondition.PublishedOn)
  val updatedOn = RangeBuilder[LocalDate, Query](RangeCondition.UpdatedOn)
  val completedOn = RangeBuilder[LocalDate, Query](range => isComplete && (updatedOn inRange range))
}
