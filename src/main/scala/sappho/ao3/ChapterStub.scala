package sappho.ao3

private class ChapterStub(val story: Story, val chapterIndex: Int, provider: ChapterInfoProvider) extends Chapter {
  override def chapterId: Long = provider.pollChapterId(chapterIndex)
  override def title: String = provider.pollTitle(chapterIndex)
}
