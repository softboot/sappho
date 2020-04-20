package sappho.queries

import sappho.Story

trait Query extends (Story => Boolean) {
  override def apply(story: Story): Boolean
}
