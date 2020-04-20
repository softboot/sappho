package sappho.ao3.tags

case class Freeform(name: String) extends sappho.tags.Freeform {
  override def sanitizedName: String = name.stripSuffix(" - Freeform")
}
