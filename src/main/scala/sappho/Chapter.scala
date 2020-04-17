package sappho

import java.net.URL
import java.time.LocalDate

trait Chapter {
  def archive: Archive
  def story: Story

  def chapterId: Long
  def chapterIndex: Int
  def url: URL

  def title: String

  def postedOn: LocalDate
}
