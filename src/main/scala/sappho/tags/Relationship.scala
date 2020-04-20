package sappho.tags

trait Relationship extends Tag {
  def isPlatonic: Boolean
  def isRomantic: Boolean
  def characters: Option[Seq[Character]]
}

object Relationship {
  def unapply(tag: Relationship): Option[String] = Some(tag.name)

  trait Platonic extends Relationship {
    override def isPlatonic: Boolean = true
    override def isRomantic: Boolean = false
    override def characters: Some[Seq[Character]]
  }
  object Platonic {
    def unapply(relationship: Relationship): Option[Seq[Character]] = {
      if(relationship.isPlatonic)
        relationship.characters
      else
        None
    }
  }

  trait Romantic extends Relationship {
    override def isPlatonic: Boolean = false
    override def isRomantic: Boolean = true
    override def characters: Some[Seq[Character]]
  }
  object Romantic {
    def unapply(relationship: Relationship): Option[Seq[Character]] = {
      if(relationship.isRomantic)
        relationship.characters
      else
        None
    }
  }
}