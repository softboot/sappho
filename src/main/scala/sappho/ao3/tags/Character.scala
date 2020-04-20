package sappho.ao3.tags

import sappho.ao3.tags.TagUtils._

case class Character(name: String) extends sappho.tags.Character {
  override def sanitizedName: String = name.stripSuffix(" - Character")
    .stripTrailingFandom()
}
