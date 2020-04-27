package sappho.queries.range

case class RangeBuilder[T, R](inRange: Range[T] => R)(implicit ordering: Ordering[T]) {
  def construct(lowerBound: Bound[T], upperBound: Bound[T]): R = inRange(Range(lowerBound, upperBound))
  
  def <(upperLimit: T): R = inRange(Range(Infinite, Exclusive(upperLimit)))
  def <=(upperLimit: T): R = inRange(Range(Infinite, Inclusive(upperLimit)))
  def >(lowerLimit: T): R = inRange(Range(Exclusive(lowerLimit), Infinite))
  def >=(lowerLimit: T): R = inRange(Range(Inclusive(lowerLimit), Infinite))
  def ==(exactValue: T): R = inRange(Range.Singleton(exactValue))
}
