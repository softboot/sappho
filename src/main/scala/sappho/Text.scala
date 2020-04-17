package sappho

trait Text {
  def paragraphs: Iterable[String]
  def fullText: String = paragraphs.mkString("\n\n")
}
