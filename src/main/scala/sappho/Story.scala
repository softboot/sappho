package sappho

import java.net.URL

trait Story {
  def archive: Archive

  def storyId: Long
  def url: URL

  def title: String

  def wordCount: Int
  def score: Int
  def views: Int
}
