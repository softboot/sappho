package sappho.ao3

private class ChapterStub(val story: Story, val chapterNumber: Int, provider: ChapterInfoProvider) extends Chapter {
  override def chapterId: Long = provider.pollChapterId(chapterNumber)
  override def title: String = provider.pollTitle(chapterNumber)
}
