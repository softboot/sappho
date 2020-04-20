package sappho.ao3.tags

case class Category(name: String) extends sappho.tags.Category {
  override def sanitizedName: String = name
}
object Category {
  val FF = Category("F/F")
  val FM = Category("F/M")
  val MM = Category("M/M")
  val Gen = Category("Gen")
  val Multi = Category("Multi")
  val Other = Category("Other")
}
