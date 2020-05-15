package sappho.ao3

private abstract class BaseChapters(progressText: String) extends Chapters {
  private val progressSplit = progressText.trim.split("/")

  override def count(): Int = progressSplit(0).toInt
  override def plannedCount: Option[Int] = progressSplit(1).toIntOption
}
