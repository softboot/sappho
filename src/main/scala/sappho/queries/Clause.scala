package sappho.queries

trait Clause extends Query {
  def conditionFor(criterion: Criterion): Option[Condition]

  def and(other: Clause): Clause
}