package sappho.queries.range

case class RangeBuilder[T, R](inRange: Range[T] => R)(implicit ordering: Ordering[T]) {
  def inRangeOption(rangeOption: Option[Range[T]]): Option[R] = rangeOption.map(inRange)
  def construct(lowerBound: Bound[T], upperBound: Bound[T]): Option[R] = inRangeOption(Range(lowerBound, upperBound))
  
  def <(upperLimit: T): R = inRange(Range(Infinite, Exclusive(upperLimit)).get)
  def <=(upperLimit: T): R = inRange(Range(Infinite, Inclusive(upperLimit)).get)
  def >(lowerLimit: T): R = inRange(Range(Exclusive(lowerLimit), Infinite).get)
  def >=(lowerLimit: T): R = inRange(Range(Inclusive(lowerLimit), Infinite).get)
  def ==(exactValue: T): R = inRange(Range.singleton(exactValue))
}
