package sappho.ao3.tags

case class Rating(name: String) extends sappho.tags.Rating {
  override def sanitizedName: String = name
}
object Rating {
  val NotRated = Rating("Not Rated")
  val GeneralAudiences = Rating("General Audiences")
  val TeenAndUpAudiences = Rating("Teen And Up Audiences")
  val Mature = Rating("Mature")
  val Explicit = Rating("Explicit")

  def all: Seq[Rating] = Seq(NotRated, GeneralAudiences, TeenAndUpAudiences, Mature, Explicit)
}