package sappho

import java.net.URL

trait Chapter {
  def archive: Archive
  def story: Story

  def chapterId: Long
  def chapterNumber: Int
  def url: URL

  def title: String
}
