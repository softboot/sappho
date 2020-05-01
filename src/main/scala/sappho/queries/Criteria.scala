package sappho.queries

object Criteria {
  val WordCount = new Criterion("wordCount")(story => Some(story.wordCount))
  val Score = new Criterion("score")(story => Some(story.score))
  val Views = new Criterion("views")(story => Some(story.views))

  val ChapterCount = new Criterion("chapterCount")(story => Some(story.chapters.length))
  val PlannedChapterCount = new Criterion("plannedChapterCount")(story => story.chapters.plannedCount)

  val PublishedOn = new Criterion("publishedOn")(story => Some(story.publishedOn))
  val UpdatedOn = new Criterion("updatedOn")(story => Some(story.updatedOn))
  
  val Complete = new Criterion("complete")(story => Some(story.isComplete))
  val OneShot = new Criterion("oneShot")(story => Some(story.isOneShot))
}
