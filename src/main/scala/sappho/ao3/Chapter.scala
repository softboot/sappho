package sappho.ao3

import java.net.URL

trait Chapter extends sappho.Chapter {
  final override def archive: Archive.type = Archive
  final override def url: URL = Chapter.urlById(story.storyId, chapterId)

  def story: Story
}

object Chapter {
  def urlById(storyId: Long, chapterId: Long): URL = {
    new URL(s"https://archiveofourown.org/works/$storyId/chapters/$chapterId")
  }
}