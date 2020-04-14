package sappho

import java.net.URL
import java.time.LocalDate

trait Story {
  def archive: Archive

  def storyId: Long
  def url: URL

  def title: String
  def language: String

  def publishedOn: LocalDate
  def updatedOn: LocalDate
  def completedOn: Option[LocalDate]

  def wordCount: Int
  def score: Int
  def views: Int

  def chapterCount: Int
  def plannedChapterCount: Option[Int]
  def isComplete: Boolean
  def isOneShot: Boolean
}
