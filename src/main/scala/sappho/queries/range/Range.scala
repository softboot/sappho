package sappho.queries.range

sealed trait Range[T] {
  def isEmpty: Boolean
  def isSingleton: Boolean
  def isUnbounded: Boolean
  def contains(value: T): Boolean
  def intersect(other: Range[T]): Range[T]
  def union(other: Range[T]): Option[Range[T]]
  def complement(): Seq[Range[T]]
  
  def toConditionString(variable: String): String
}
object Range {
  def apply[T](lowerBound: Bound[T], upperBound: Bound[T])(implicit ordering: Ordering[T]): Range[T] = {
    if(isValidRange(lowerBound, upperBound))
      new NotEmpty(lowerBound, upperBound)
    else
      Empty()
  }

  def unapply[T](range: NotEmpty[T]): Option[(Bound[T], Bound[T])] = {
    Some((range.lowerBound, range.upperBound))
  }

  final case class Empty[T]()(implicit ordering: Ordering[T]) extends Range[T] {
    override def isEmpty: Boolean = true
    override def isSingleton: Boolean = false
    override def isUnbounded: Boolean = false
    override def contains(value: T) = false
    override def intersect(other: Range[T]) = this
    override def union(other: Range[T]) = Some(other)
    override def complement(): Seq[Range[T]] = Seq(Range[T](Infinite, Infinite))
    override def toString = "(empty range)"
    override def toConditionString(variable: String): String = s"contradiction[$variable]"
  }

  object Singleton {
    def apply[T](onlyElement: T)(implicit ordering: Ordering[T]): Range[T] = {
      new NotEmpty(Inclusive(onlyElement), Inclusive(onlyElement))
    }

    def unapply[T](range: NotEmpty[T]): Option[T] = {
      if(range.isSingleton)
        range.lowerBound.bound
      else
        None
    }
  }

  object Unbounded {
    def apply[T]()(implicit ordering: Ordering[T]): Range.NotEmpty[T] = new NotEmpty[T](Infinite, Infinite)

    def unapply[T](range: NotEmpty[T]): Boolean = range.isUnbounded
  }

  final class NotEmpty[T](val lowerBound: Bound[T], val upperBound: Bound[T])(implicit ordering: Ordering[T]) extends Range[T] {
    import ordering.mkOrderingOps

    override def isEmpty: Boolean = false

    override def isSingleton: Boolean = lowerBound.isInclusive && lowerBound == upperBound

    override def isUnbounded: Boolean = lowerBound.isInfinite && upperBound.isInfinite

    override def contains(value: T): Boolean = fitsLeft(value) && fitsRight(value)

    override def intersect(other: Range[T]): Range[T] = other match {
      case empty@Empty() => empty
      case _ => intersect(other.asInstanceOf[NotEmpty[T]])
    }

    def intersect(other: NotEmpty[T]): Range[T] = {
      val (lower, _) = Bound.pickTightLoose(this.lowerBound, other.lowerBound)
      val (upper, _) = Bound.pickTightLoose(this.upperBound, other.upperBound)(ordering.reverse)
      Range(lower, upper)
    }

    override def union(other: Range[T]): Option[Range[T]] = other match {
      case Empty() => Some(this)
      case _ => union(other.asInstanceOf[NotEmpty[T]])
    }

    def union(other: NotEmpty[T]): Option[Range[T]] = {
      val (lowerTight, lowerLoose) = Bound.pickTightLoose(this.lowerBound, other.lowerBound)
      val (upperTight, upperLoose) = Bound.pickTightLoose(this.upperBound, other.upperBound)(ordering.reverse)

      if(Bound.isUnionCoherent(lowerTight, upperTight))
        Some(Range(lowerLoose, upperLoose))
      else
        None
    }

    override def complement(): Seq[Range[T]] = (lowerBound, upperBound) match {
      case (Infinite, Infinite) => Seq()

      case (Infinite, Inclusive(b)) => Seq(Range[T](Exclusive(b), Infinite))
      case (Infinite, Exclusive(b)) => Seq(Range[T](Inclusive(b), Infinite))
      case (Inclusive(a), Infinite) => Seq(Range[T](Infinite, Exclusive(a)))
      case (Exclusive(a), Infinite) => Seq(Range[T](Infinite, Inclusive(a)))

      case (f1, f2) => Range[T](f1, Infinite).complement() ++ Range[T](Infinite, f2).complement()
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

    override def toConditionString(variable: String): String = (lowerBound, upperBound) match {
      case (Infinite, Infinite) => s"tautology[$variable]"

      case (Infinite, Exclusive(b)) => s"$variable < $b"
      case (Infinite, Inclusive(b)) => s"$variable <= $b"

      case (Exclusive(a), Infinite) => s"$variable > $a"
      case (Inclusive(a), Infinite) => s"$variable >= $a"

      case (f1@Finite(a), f2@Finite(b)) =>
        val left = if(f1.isExclusive) "<" else "<="
        val right = if(f2.isExclusive) "<" else "<="
        s"$a $left $variable $right $b"
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
      case that: NotEmpty[T] => this.lowerBound == that.lowerBound && this.upperBound == that.upperBound
      case _ => false
    }
    override def hashCode(): Int = {
      31 * lowerBound.hashCode + upperBound.hashCode
    }
  }


  def element[T](implicit ordering: Ordering[T]) = RangeBuilder[T, Range[T]](e => e)


  private def isValidRange[T](lowerBound: Bound[T], upperBound: Bound[T])(implicit ordering: Ordering[T]): Boolean = {
    import ordering.mkOrderingOps

    (lowerBound, upperBound) match {
      case (Infinite, _) | (_, Infinite) => true
      case (Inclusive(a), Inclusive(b)) => a <= b
      case (Finite(a), Finite(b)) => a < b
    }
  }
}