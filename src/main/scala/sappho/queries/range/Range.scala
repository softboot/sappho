package sappho.queries.range

sealed trait Range[T] {
  def isEmpty: Boolean
  def contains(value: T): Boolean
  def intersect(other: Range[T]): Range[T]
  def union(other: Range[T]): Range[T]
}

final case class EmptyR[T]() extends Range[T] {
  override def isEmpty: Boolean = true
  override def contains(value: T) = false
  override def intersect(other: Range[T]) = this
  override def union(other: Range[T]) = other
  override def toString = "(empty range)"
}

final class NotEmptyR[T](val lowerBound: Bound[T], val upperBound: Bound[T])(implicit ordering: Ordering[T]) extends Range[T] {
  import ordering.mkOrderingOps

  override def isEmpty: Boolean = false

  override def contains(value: T): Boolean = fitsLeft(value) && fitsRight(value)

  override def intersect(other: Range[T]): Range[T] = other match {
    case empty@EmptyR() => empty
    case _ => intersect(other.asInstanceOf[NotEmptyR[T]])
  }

  def intersect(other: NotEmptyR[T]): Range[T] = {
    val (lower, _) = Bound.pickTightLoose(this.lowerBound, other.lowerBound)
    val (upper, _) = Bound.pickTightLoose(this.upperBound, other.upperBound)(ordering.reverse)
    Range(lower, upper)
  }

  override def union(other: Range[T]): Range[T] = other match {
    case EmptyR() => this
    case _ => union(other.asInstanceOf[NotEmptyR[T]])
  }

  def union(other: NotEmptyR[T]): Range[T] = {
    val (lowerTight, lowerLoose) = Bound.pickTightLoose(this.lowerBound, other.lowerBound)
    val (upperTight, upperLoose) = Bound.pickTightLoose(this.upperBound, other.upperBound)(ordering.reverse)

    if(Bound.isUnionCoherent(lowerTight, upperTight))
      Range(lowerLoose, upperLoose)
    else
      EmptyR()
  }

  private def fitsLeft(value: T): Boolean = lowerBound match {
    case Inclusive(a) => a <= value
    case Exclusive(a) => a < value
    case Infinite => true
  }

  private def fitsRight(value: T): Boolean = upperBound match {
    case Inclusive(b) => value <= b
    case Exclusive(b) => value < b
    case Infinite => true
  }

  override def toString: String = {
    val left = lowerBound match {
      case Inclusive(a) => "[" + a
      case Exclusive(a) => "(" + a
      case Infinite => "(-Inf"
    }
    val right = upperBound match {
      case Inclusive(a) => a + "]"
      case Exclusive(a) => a + ")"
      case Infinite => "+Inf)"
    }
    left + ";" + right
  }

  override def equals(other: Any): Boolean = other match {
    case that: NotEmptyR[T] => this.lowerBound == that.lowerBound && this.upperBound == that.upperBound
    case _ => false
  }
  override def hashCode(): Int = {
    31 * lowerBound.hashCode + upperBound.hashCode
  }
}

object Range {
  def singleton[T](onlyElement: T)(implicit ordering: Ordering[T]): Range[T] = {
    new NotEmptyR(Inclusive(onlyElement), Inclusive(onlyElement))
  }

  def apply[T](lowerBound: Bound[T], upperBound: Bound[T])(implicit ordering: Ordering[T]): Range[T] = {
    if(isValidRange(lowerBound, upperBound))
      new NotEmptyR(lowerBound, upperBound)
    else
      EmptyR()
  }

  def unapply[T](range: NotEmptyR[T]): Option[(Bound[T], Bound[T])] = {
    Some((range.lowerBound, range.upperBound))
  }

  private def isValidRange[T](lowerBound: Bound[T], upperBound: Bound[T])(implicit ordering: Ordering[T]): Boolean = {
    import ordering.mkOrderingOps

    (lowerBound, upperBound) match {
      case (Infinite, _) | (_, Infinite) => true
      case (Inclusive(a), Inclusive(b)) => a <= b
      case (Finite(a), Finite(b)) => a < b
    }
  }

  def element[T](implicit ordering: Ordering[T]) = RangeBuilder[T, Range[T]](e => e)
}