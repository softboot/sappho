package sappho.queries

import sappho.Story

trait Query extends (Story => Boolean) {
  override def apply(story: Story): Boolean

  def criteria: Set[Criterion]

  def and(other: Query): Query

  final def &&(other: Query) = this and other
}
