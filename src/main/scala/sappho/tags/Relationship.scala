package sappho.tags

trait Relationship extends Tag {
  def isPlatonic: Boolean
  def isRomantic: Boolean
  def characters: Option[Seq[String]]
}

object Relationship {
  def unapply(tag: Relationship): Option[String] = Some(tag.name)

  trait Platonic extends Relationship {
    override def isPlatonic: Boolean = true
    override def isRomantic: Boolean = false
    override def characters: Some[Seq[String]]
  }
  object Platonic {
    def unapply(relationship: Relationship): Option[Seq[String]] = {
      if(relationship.isPlatonic)
        relationship.characters
      else
        None
    }
  }

  trait Romantic extends Relationship {
    override def isPlatonic: Boolean = false
    override def isRomantic: Boolean = true
    override def characters: Some[Seq[String]]
  }
  object Romantic {
    def unapply(relationship: Relationship): Option[Seq[String]] = {
      if(relationship.isRomantic)
        relationship.characters
      else
        None
    }
  }

  trait Other extends Relationship {
    final override def isPlatonic: Boolean = false
    final override def isRomantic: Boolean = false
    final override def characters: None.type = None
  }
  object Other {
    def unapply(relationship: Relationship): Option[String] = {
      if(relationship.isRomantic || relationship.isPlatonic)
        None
      else
        Some(relationship.name)
    }
  }
}