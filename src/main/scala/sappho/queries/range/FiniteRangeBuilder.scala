package sappho.queries.range

class FiniteRangeBuilder[T, R](lowerBound: Bound[T], rangeBuilder: RangeBuilder[T, R]) {
  def <(upperLimit: T): R = rangeBuilder.construct(lowerBound, Exclusive(upperLimit))
  def <=(upperLimit: T): R = rangeBuilder.construct(lowerBound, Inclusive(upperLimit))

  override def toString = lowerBound match {
    case Inclusive(a) => s"<$a|"
    case Exclusive(a) => s"($a|"
  }
}
object FiniteRangeBuilder {
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
}