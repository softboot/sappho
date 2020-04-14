package sappho

import java.net.URL

trait Story {
  def archive: Archive

  def storyId: Long
  def url: URL

  def title: String
  def language: String

  def wordCount: Int
  def score: Int
  def views: Int

  def chapterCount: Int
  def plannedChapterCount: Option[Int]
  def isComplete: Boolean
  def isOneShot: Boolean
}
