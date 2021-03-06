package sappho.queries

import sappho.Story

trait Query extends (Story => Boolean) {
  override def apply(story: Story): Boolean

  def criteria: Set[Criterion[Any]]

  def and(other: Query): Query

  def or(other: Query): Query

  def not(): Query

  def normalized: Query

  final def &&(other: Query) = this and other
  final def ||(other: Query) = this or other
  final def unary_!() = not()
}
