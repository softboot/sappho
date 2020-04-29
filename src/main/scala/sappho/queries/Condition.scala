package sappho.queries

trait Condition extends Clause {
  def criterion: Criterion

  override def conditionFor(criterion: Criterion): Option[Condition] = {
    if(criterion == this.criterion)
      Some(this)
    else
      None
  }
}
