package sappho.ao3

import java.net.URL

trait Story extends sappho.Story {
  final override def archive: Archive.type = Archive

  final override def url: URL = Story.urlByStoryId(storyId)
}

object Story {
  def urlByStoryId(storyId: Long): URL = new URL(s"https://archiveofourown.org/works/$storyId/")
}