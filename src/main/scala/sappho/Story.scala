package sappho

import java.net.URL

trait Story {
  def archive: Archive

  def storyId: Long
  def url: URL

  def title: String
}
