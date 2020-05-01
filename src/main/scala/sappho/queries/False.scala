package sappho.queries
import sappho.Story

case object False extends Clause {
  override def apply(story: Story) = false
  override def criteria = Set.empty
  override def conditionFor(criterion: Criterion) = None
  override def and(other: Clause) = this
  override def and(other: Query) = this
  override def tryOr(other: Clause) = Some(other)
  override def or(other: Query) = other
  override def not() = True
  override def toString() = "False"
}
