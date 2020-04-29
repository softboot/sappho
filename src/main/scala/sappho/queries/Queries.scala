package sappho.queries

import java.time.LocalDate

import sappho.queries.range.{Exclusive, FiniteRangeBuilder, Inclusive, RangeBuilder}

object Queries {
  val wordCount = RangeBuilder[Int, RangeCondition[Int]](RangeCondition.WordCount)
  val score = RangeBuilder[Int, RangeCondition[Int]](RangeCondition.Score)
  val views = RangeBuilder[Int, RangeCondition[Int]](RangeCondition.Views)

  val chapterCount = RangeBuilder[Int, RangeCondition[Int]](RangeCondition.ChapterCount)
  val plannedChapterCount = RangeBuilder[Int, RangeCondition[Int]](RangeCondition.PlannedChapterCount)

  val isComplete = BooleanCondition.Complete
  val isOneShot = BooleanCondition.OneShot

  val publishedOn = RangeBuilder[LocalDate, RangeCondition[LocalDate]](RangeCondition.PublishedOn)
  val updatedOn = RangeBuilder[LocalDate, RangeCondition[LocalDate]](RangeCondition.UpdatedOn)


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
}
