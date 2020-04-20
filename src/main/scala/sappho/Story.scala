package sappho

import java.net.URL
import java.time.LocalDate

import sappho.tags.Tags

trait Story extends Text with Tags {
  def archive: Archive

  def storyId: Long
  def url: URL

  def title: String
  def authors: Seq[Author]
  def language: String

  def publishedOn: LocalDate
  def updatedOn: LocalDate
  def completedOn: Option[LocalDate]

  def wordCount: Int
  def score: Int
  def views: Int

  def isComplete: Boolean
  def isOneShot: Boolean

  def chapters: Chapters

  override def paragraphs: Iterable[String] = chapters.iterator
    .flatMap(ch => ch.paragraphs)
    .to(Iterable)
}
