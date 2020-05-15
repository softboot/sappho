package sappho.ao3.tags

private object TagUtils {
  implicit class ExtendedString(val tag: String) extends AnyVal {
    import ExtendedString._

    def stripTrailingFandom(): String = {
      tag match {
        case trailingFandomPattern(stripped) => stripped
        case _ => tag
      }
    }
  }
  object ExtendedString {
    private val trailingFandomPattern = """^(.*) \(.*\)$""".r
  }
}
