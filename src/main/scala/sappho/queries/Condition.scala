package sappho.queries

trait Condition extends Clause {
  def criterion: Criterion[Any]

  override final def criteria: Set[Criterion[Any]] = Set(criterion)

  override def conditionFor(criterion: Criterion[Any]): Option[Condition] = {
    if(criterion == this.criterion)
      Some(this)
    else
      None
  }
}
