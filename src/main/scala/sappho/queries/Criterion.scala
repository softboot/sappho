package sappho.queries

import sappho.Story

final class Criterion[+T](val name: String)(extractor: Story => Option[T]) extends (Story => Option[T]) {
  def apply(story: Story): Option[T] = extractor(story)

  override def equals(other: Any): Boolean = other match {
    case that: Criterion[T] => name == that.name
    case _ => false
  }

  override def hashCode(): Int = name.hashCode

  override def toString() = s"Criterion($name)"
}

object Criterion {
  def unapply[T](criterion: Criterion[T]): Option[String] = Some(criterion.name)
}