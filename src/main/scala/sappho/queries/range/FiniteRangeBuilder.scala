package sappho.queries.range

class FiniteRangeBuilder[T, R](lowerBound: Bound[T], rangeBuilder: RangeBuilder[T, R]) {
  def <(upperLimit: T): R = rangeBuilder.construct(lowerBound, Exclusive(upperLimit))
  def <=(upperLimit: T): R = rangeBuilder.construct(lowerBound, Inclusive(upperLimit))

  override def toString = lowerBound match {
    case Inclusive(a) => s"<$a|"
    case Exclusive(a) => s"($a|"
  }
}