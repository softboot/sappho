package sappho.tags

trait Tags {
  def tags: Iterable[Tag]

  def ratings: Iterable[Rating]
  def warnings: Iterable[Warning]
  def categories: Iterable[Category]
  def genres: Iterable[Genre]
  def fandoms: Iterable[Fandom]
  def characters: Iterable[Character]
  def relationships: Iterable[Relationship]
  def freeform: Iterable[Freeform]
}
