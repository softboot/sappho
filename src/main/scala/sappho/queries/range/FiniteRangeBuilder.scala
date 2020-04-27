package sappho.queries.range

class FiniteRangeBuilder[T, R](lowerBound: Bound[T], rangeBuilder: RangeBuilder[T, R]) {
  def <(upperLimit: T): Option[R] = rangeBuilder.construct(lowerBound, Exclusive(upperLimit))
  def <=(upperLimit: T): Option[R] = rangeBuilder.construct(lowerBound, Inclusive(upperLimit))

  override def toString = lowerBound match {
    case Inclusive(a) => s"<$a|"
    case Exclusive(a) => s"($a|"
  }
}
object FiniteRangeBuilder {
  implicit class IntLHS(leftLimit: Int) {
    def <[R](rangeBuilder: RangeBuilder[Int, R]): FiniteRangeBuilder[Int, R] = {
      new FiniteRangeBuilder[Int, R](Exclusive(leftLimit), rangeBuilder)
    }
    def <=[R](rangeBuilder: RangeBuilder[Int, R]): FiniteRangeBuilder[Int, R] = {
      new FiniteRangeBuilder[Int, R](Inclusive(leftLimit), rangeBuilder)
    }
  }
}