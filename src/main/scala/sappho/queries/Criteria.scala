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

  val Ratings = new Criterion("ratings")(story => Some(story.ratings.map(_.name).to(Set)))
  val Warnings = new Criterion("warnings")(story => Some(story.warnings.map(_.name).to(Set)))
  val Categories = new Criterion("categories")(story => Some(story.categories.map(_.name).to(Set)))
  val Genres = new Criterion("genres")(story => Some(story.genres.map(_.name).to(Set)))
  val Fandoms = new Criterion("fandoms")(story => Some(story.fandoms.map(_.name).to(Set)))
  val Characters = new Criterion("characters")(story => Some(story.characters.map(_.name).to(Set)))
  val Relationships = new Criterion("relationships")(story => Some(story.relationships.map(_.name).to(Set)))
  val FreeformTags = new Criterion("freeform")(story => Some(story.freeform.map(_.name).to(Set)))
}
