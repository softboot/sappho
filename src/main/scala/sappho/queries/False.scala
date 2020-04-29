package sappho.queries
import sappho.Story

case object False extends Clause {
  override def apply(story: Story) = false
  override def conditionFor(criterion: Criterion) = None
  override def and(other: Clause) = this
  override def and(other: Query) = this
  override def toString() = "False"
}
