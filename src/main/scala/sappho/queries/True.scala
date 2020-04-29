package sappho.queries

import sappho.Story

case object True extends Query {
  override def apply(story: Story) = true
  override def and(other: Query) = other
  override def toString() = "True"
}

