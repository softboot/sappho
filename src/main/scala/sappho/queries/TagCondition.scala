package sappho.queries
import sappho.Story

class TagCondition private(val criterion: Criterion[Set[String]], val includedTags: Set[String], val excludedTags: Set[String])
  extends Condition
{
  import TagCondition._

  override def apply(story: Story): Boolean = criterion(story) match {
    case Some(storyTags) => includedTags.subsetOf(storyTags) && excludedTags.intersect(storyTags) == Set.empty[String]
    case None => false
  }

  override def and(clause: Clause): Clause = clause match {
    case True => this
    case False => False
    case anAnd: And => anAnd.and(this)
    case other: TagCondition if other.criterion == this.criterion =>
      TagCondition(criterion)(includedTags ++ other.includedTags, excludedTags ++ other.excludedTags)
    case different: Condition => And(this, different)
  }

  def subsetOf(other: TagCondition): Boolean = {
    this.criterion == other.criterion &&
      this.includedTags.subsetOf(other.includedTags) &&
      this.excludedTags.subsetOf(other.excludedTags) &&
      ((this.includedTags intersect other.excludedTags) == Set.empty[String] ||
      (this.excludedTags intersect other.includedTags) == Set.empty[String])
  }

  override def tryOr(clause: Clause): Option[Clause] = clause match {
    case True | False | And(_) => clause.tryOr(this)
    case other: TagCondition if other.criterion == this.criterion =>
      if(this.subsetOf(other))
        Some(TagCondition(criterion)(this.includedTags -- other.excludedTags, this.excludedTags -- other.includedTags))
      else if(other.subsetOf(this))
        Some(TagCondition(criterion)(other.includedTags -- this.excludedTags, other.excludedTags -- this.includedTags))
      else
        None
    case _: Condition => None
  }

  override def not(): Query = {
    (
      includedTags.iterator.map(i => TagCondition(criterion)(excluded = Set(i))) ++
      excludedTags.iterator.map(e => TagCondition(criterion)(included = Set(e)))
    ).foldLeft(False.asInstanceOf[Query])(_ or _)
  }

  override def normalized: Clause = {
    if(includedTags.isEmpty && excludedTags.isEmpty) True
    else if((includedTags intersect excludedTags).nonEmpty) False
    else this
  }


  override def equals(other: Any): Boolean = other match {
    case that: TagCondition =>
        this.criterion == that.criterion &&
        this.includedTags == that.includedTags &&
        this.excludedTags == that.excludedTags
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(criterion, includedTags, excludedTags)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString: String = {
    val parts = Seq(
      constraintsToString(includedTags, "includes", "includesAll"),
      constraintsToString(excludedTags, "excludes", "excludesAll")
    ).filter(_.nonEmpty)

    if(parts.isEmpty)
      "tautology[" + criterion.name + "]"
    else
      criterion.name + " " + parts.mkString(" and ")
  }
}

object TagCondition {
  def apply(criterion: Criterion[Set[String]])(included: Set[String] = Set.empty, excluded: Set[String] = Set.empty): Clause = {
    if(included.isEmpty && excluded.isEmpty) True
    else if((included intersect excluded).nonEmpty) False
    else new TagCondition(criterion, included, excluded)
  }

  class Builder private(criterion: Criterion[Set[String]]) {
    def includes(tag: String): TagCondition = new TagCondition(criterion, Set(tag), Set.empty)
    def includesAll(tags: String*): Clause = includesAllOf(tags)
    def includesAllOf(tags: Iterable[String]): Clause = TagCondition(criterion)(included = tags.to(Set))

    def excludes(tag: String): TagCondition = new TagCondition(criterion, Set.empty, Set(tag))
    def excludesAll(tags: String*): Clause = excludesAllOf(tags)
    def excludesAllOf(tags: Iterable[String]): Clause = TagCondition(criterion)(excluded = tags.to(Set))
  }
  object Builder {
    def apply(criterion: Criterion[Set[String]]): Builder = new Builder(criterion)
  }
  

  private def constraintsToString(constraints: Set[String], oneFlavorText: String, manyFlavorText: String): String = {
    constraints.size match {
      case 0 => ""
      case 1 => oneFlavorText + " \"" + constraints.head + '"'
      case _ => manyFlavorText + " " + constraints.iterator.map('"' + _ + '"').mkString("[", ", ", "]")
    }
  }
}