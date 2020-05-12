package sappho.ao3

import java.time.LocalDate

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._

private class SectionChapter(val story: Story, val chapterIndex: Int, root: Element, provider: ChapterInfoProvider)
  extends Chapter
{
  override def chapterId: Long = provider.pollChapterId(chapterIndex)

  override def postedOn: LocalDate = provider.pollDate(chapterIndex)

  override def title: String = (root >> element("h3.title"))
    .childNodes
    .collect { case t: TextNode => t.content.strip() }
    .filterNot(_.isEmpty)
    .map(s => s.substring(2))
    .headOption
    .getOrElse("Chapter " + (chapterIndex + 1))

  override def paragraphs: Iterable[String] = (root >> texts("div[role=article] p"))



  override def equals(other: Any): Boolean = other match {
    case that: sappho.ao3.Chapter =>
        this.story.storyId == that.story.storyId &&
        this.chapterIndex == that.chapterIndex
    case _ => false
  }

  override def hashCode(): Int = 31 * story.hashCode + chapterIndex
}
