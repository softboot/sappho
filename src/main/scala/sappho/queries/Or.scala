package sappho.queries
import sappho.Story

class Or(private val clauses: Set[Clause]) extends Query {
  override def apply(story: Story) = clauses.exists(_(story))

  override def criteria: Set[Criterion] = clauses.map(_.criteria).fold(Set.empty)(_ ++ _)

  override def and(other: Query): Query = other match {
    case True => this
    case False => False
    case _ => clauses.iterator
      .map(_ and other)
      .foldLeft(new Or(Set.empty).asInstanceOf[Query])(_ or _)
      .normalized
  }

  override def or(other: Query): Query = other match {
    case True => True
    case False => this
    case singleClause: Clause =>
      val (resultSet, lastElement) = clauses.foldLeft((Set.empty[Clause], singleClause))(
        (acc: (Set[Clause], Clause), nextClause: Clause) => {
          val (unrelatedClauses, newOne) = acc
          (nextClause tryOr newOne)
            .map(union => (unrelatedClauses, union))
            .getOrElse((unrelatedClauses + nextClause, newOne))
        }
      )
      new Or(resultSet + lastElement)
    case another: Or => another.clauses
      .foldLeft(this.asInstanceOf[Query])(_ or _)
  }

  override def not(): Query = clauses.iterator
    .map(_.not())
    .foldLeft(True.asInstanceOf[Query])(_ and _)

  override def normalized: Query = clauses.size match {
    case 0 => True
    case 1 => clauses.head
    case _ => this
  }

  override def toString(): String = clauses.mkString(" || ")

  override def equals(other: Any): Boolean = other match {
    case that: Or => this.clauses == that.clauses
    case _ => false
  }

  override def hashCode(): Int = clauses.hashCode
}
object Or {
  def apply(clauses: Clause*): Query = clauses
    .foldLeft(new Or(Set.empty).asInstanceOf[Query])(_ or _)
    .normalized

  def unapply(or: Or): Option[Iterable[Clause]] = Some(or.clauses)
}