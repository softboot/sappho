package sappho.ao3

import java.net.URL

import sappho.Story

trait Chapter extends sappho.Chapter {
  final override def archive: Archive.type = Archive
  final override def url: URL = Chapter.urlById(story.storyId, chapterId)

  def story: Story

  def chapterId: Long
  def chapterNumber: Int

  def title: String
}

object Chapter {
  def urlById(storyId: Long, chapterId: Long): URL = {
    new URL(s"https://archiveofourown.org/works/$storyId/chapters/$chapterId")
  }
}