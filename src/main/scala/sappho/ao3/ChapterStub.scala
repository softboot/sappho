package sappho.ao3
import java.time.LocalDate

private class ChapterStub(val story: Story, val chapterIndex: Int, provider: ChapterInfoProvider) extends Chapter {
  override def chapterId: Long = provider.pollChapterId(chapterIndex)
  override def title: String = provider.pollTitle(chapterIndex)
  override def postedOn: LocalDate = provider.pollDate(chapterIndex)

  override def paragraphs: Iterable[String] = ???
}
