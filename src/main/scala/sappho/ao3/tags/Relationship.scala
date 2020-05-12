package sappho.ao3.tags
import sappho.ao3.tags.TagUtils._

case class Relationship(name: String) extends sappho.tags.Relationship {
  import Relationship._
  
  override val sanitizedName: String = name.stripSuffix(" - Relationship")
    .stripTrailingFandom()

  override def isPlatonic: Boolean = sanitizedName.contains(platonicSeparator) &&
    !sanitizedName.contains(romanticSeparator)

  override def isRomantic: Boolean = sanitizedName.contains(romanticSeparator) &&
    !sanitizedName.contains(platonicSeparator)

  override def characters: Option[Seq[String]] = {
    if(isPlatonic || isRomantic)
      Some(sanitizedName.split(separatorPattern).toIndexedSeq)
    else
      None
  }
}
object Relationship {
  private val platonicSeparator = " & "
  private val romanticSeparator = "/"
  private val separatorPattern = "(" + platonicSeparator + "|" + romanticSeparator + ")"
}