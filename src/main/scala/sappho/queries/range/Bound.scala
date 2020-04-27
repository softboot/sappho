package sappho.queries.range

sealed trait Bound[+T] {
  def bound: Option[T]

  def isFinite: Boolean = false
  def isInclusive: Boolean = false
  def isExclusive: Boolean = false
  def isInfinite: Boolean = false
}
object Bound {
  private[queries] def pickTightLoose[T](b1: Bound[T], b2: Bound[T])(implicit ordering: Ordering[T]): (Bound[T], Bound[T]) = {
    (b1, b2) match {
      case (tight, loose@Infinite) => (tight, loose)
      case (loose@Infinite, tight) => (tight, loose)

      case (Finite(x1), Finite(x2)) =>
        val cmp = ordering.compare(x1, x2)
        if(cmp == 0) {
          if(b1.isInclusive && b2.isInclusive)
            (b2, b1)
          else
            (b1, b2)
        }
        else {
          if (cmp < 0 /* x1 < x2 */ )
            (b2, b1)
          else /* if x1 > x2 */
            (b1, b2)
        }
    }
  }

  private[queries] def isUnionCoherent[T](lowerTight: Bound[T], upperTight: Bound[T])(implicit ordering: Ordering[T]): Boolean = {
    (lowerTight, upperTight) match {
      case (Finite(a), Finite(b)) =>
        val cmp = ordering.compare(a, b)
        if(cmp == 0)
          !lowerTight.isExclusive || !upperTight.isExclusive
        else
          cmp > 0
      case _ => true
    }
  }
}

abstract sealed class Finite[+T] extends Bound[T] {
  def limit: T
  final override def bound: Some[T] = Some(limit)
  final override def isFinite: Boolean = true
}
object Finite {
  def unapply[T](finiteBound: Finite[T]): Option[T] = Some(finiteBound.limit)
}

final case class Inclusive[+T](limit: T) extends Finite[T] {
  override def isInclusive: Boolean = true
}

final case class Exclusive[+T](limit: T) extends Finite[T] {
  override def isExclusive: Boolean = true
}

case object Infinite extends Bound[Nothing] {
  override def bound: None.type = None
  override def isInfinite: Boolean = true
}