package sappho.ao3.tags

case class Warning(name: String) extends sappho.tags.Warning {
  override def sanitizedName: String = name
}
object Warning {
  val NoneApply = Warning("No Archive Warnings Apply")
  val NoneUsed = Warning("Creator Chose Not To Use Archive Warnings")
}