package sappho.queries

import sappho.Story

case object True extends Clause {
  override def apply(story: Story) = true
  override def criteria = Set.empty
  override def conditionFor(criterion: Criterion) = None
  override def and(other: Query) = other
  override def and(other: Clause) = other
  override def tryOr(other: Clause) = Some(True)
  override def or(other: Query) = True
  override def toString() = "True"
}

