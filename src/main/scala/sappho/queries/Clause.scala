package sappho.queries

trait Clause extends Query {
  def conditionFor(criterion: Criterion): Option[Condition]

  def and(other: Clause): Clause

  def tryOr(other: Clause): Option[Clause]

  override def or(other: Query): Query = other match {
    case clause: Clause => (this tryOr clause).getOrElse(new Or(Set(this, clause)))
    case _: Or => other or this
  }
}
