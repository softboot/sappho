package sappho.queries

import sappho.Story

trait Query extends (Story => Boolean) {
  override def apply(story: Story): Boolean

  def and(other: Query): Query

  final def &&(other: Query) = this and other
}
