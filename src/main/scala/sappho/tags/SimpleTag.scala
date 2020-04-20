package sappho.tags

trait SimpleTag extends Tag

trait SimpleTagCompanion[T <: SimpleTag] {
  def unapply(tag: T): Option[String] = Some(tag.name)
}

trait Rating extends SimpleTag
object Rating extends SimpleTagCompanion[Rating]

trait Warning extends SimpleTag
object Warning extends SimpleTagCompanion[Warning]

trait Category extends SimpleTag
object Category extends SimpleTagCompanion[Category]

trait Genre extends SimpleTag
object Genre extends SimpleTagCompanion[Genre]

trait Fandom extends SimpleTag
object Fandom extends SimpleTagCompanion[Fandom]

trait Character extends SimpleTag
object Character extends SimpleTagCompanion[Character]

trait Freeform extends SimpleTag
object Freeform extends SimpleTagCompanion[Freeform]
