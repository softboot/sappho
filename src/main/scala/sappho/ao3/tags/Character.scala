package sappho.ao3.tags

case class Character(name: String) extends sappho.tags.Character {
  override def sanitizedName: String = name.stripSuffix(" - Character")
}
