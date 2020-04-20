package sappho.ao3.tags

private object TagUtils {
  implicit class ExtendedString(tag: String) {
    private val trailingFandomPattern = """^(.*) \(.*\)$""".r

    def stripTrailingFandom(): String = {
      tag match {
        case trailingFandomPattern(stripped) => stripped
        case _ => tag
      }
    }
  }
}
