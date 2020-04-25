package sappho.queries

final class Range[T] private(val lowerBound: Bound[T], val upperBound: Bound[T])(implicit ordering: Ordering[T]) {
  import ordering.mkOrderingOps

  def contains(value: T): Boolean = fitsLeft(value) && fitsRight(value)

  def intersect(other: Range[T]): Option[Range[T]] = {
    val (lower, _) = Bound.pickTightLoose(this.lowerBound, other.lowerBound)
    val (upper, _) = Bound.pickTightLoose(this.upperBound, other.upperBound)(ordering.reverse)
    Range(lower, upper)
  }

  def union(other: Range[T]): Option[Range[T]] = {
    val (lowerTight, lowerLoose) = Bound.pickTightLoose(this.lowerBound, other.lowerBound)
    val (upperTight, upperLoose) = Bound.pickTightLoose(this.upperBound, other.upperBound)(ordering.reverse)

    if(Bound.isUnionCoherent(lowerTight, upperTight))
      Range(lowerLoose, upperLoose)
    else
      None
  }

  private def fitsLeft(value: T): Boolean = lowerBound match {
    case Inclusive(a) => a <= value
    case Exclusive(a) => a < value
    case Infinite() => true
  }

  private def fitsRight(value: T): Boolean = upperBound match {
    case Inclusive(b) => value <= b
    case Exclusive(b) => value < b
    case Infinite() => true
  }

  override def toString: String = {
    val left = lowerBound match {
      case Inclusive(a) => "[" + a
      case Exclusive(a) => "(" + a
      case Infinite() => "(-Inf"
    }
    val right = upperBound match {
      case Inclusive(a) => a + "]"
      case Exclusive(a) => a + ")"
      case Infinite() => "+Inf)"
    }
    left + ";" + right
  }

  override def equals(other: Any): Boolean = other match {
    case that: Range[T] => this.lowerBound == that.lowerBound && this.upperBound == that.upperBound
    case _ => false
  }
  override def hashCode(): Int = {
    31 * lowerBound.hashCode + upperBound.hashCode
  }
}
object Range {
  def apply[T](lowerBound: Bound[T], upperBound: Bound[T])(implicit ordering: Ordering[T]): Option[Range[T]] = {
    if(isValidRange(lowerBound, upperBound))
      Some(new Range(lowerBound, upperBound))
    else
      None
  }

  def unapply[T](range: Range[T]): Option[(Bound[T], Bound[T])] = {
    Some((range.lowerBound, range.upperBound))
  }

  private def isValidRange[T](lowerBound: Bound[T], upperBound: Bound[T])(implicit ordering: Ordering[T]): Boolean = {
    import ordering.mkOrderingOps

    (lowerBound, upperBound) match {
      case (Infinite(), _) | (_, Infinite()) => true
      case (Inclusive(a), Inclusive(b)) => a <= b
      case (Finite(a), Finite(b)) => a < b
    }
  }
}