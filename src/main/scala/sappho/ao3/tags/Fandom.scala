package sappho.ao3.tags

case class Fandom(name: String) extends sappho.tags.Fandom {
  override def sanitizedName: String = name.stripSuffix(" - Fandom")
}
