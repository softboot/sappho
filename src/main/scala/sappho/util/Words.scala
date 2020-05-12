package sappho.util

import scala.util.matching.Regex

/** Provides utilities for extracting words or lexemes from strings. */
object Words {
  /** Regular expression representing a single lexeme. */
  val lexemePattern: Regex = raw"[^\s\p{Punct}]+(['-][^\s\p{Punct}]+)*".r

  /** Checks if a given lexeme represents a word.
   *
   *  If the provided input does not represent a single lexeme, the result
   *  is not guaranteed to be correct.
   */
  def isWord(lexeme: String): Boolean = !(lexeme forall Character.isDigit)

  implicit class ExtendedString(val string: String) extends AnyVal {
    /** Splits a string into lexemes.
     *
     *  In the context of this library, a lexeme is any sequence of characters
     *  surrounded by whitespace, punctuation, or paragraph boundaries.
     *  Hyphens and apostrophes within a lexeme are allowed (for instance,
     *  "well-read" and "let's" both constitute a single lexeme); however,
     *  trailing hyphens and apostrophes are treated as punctuation.
     *
     *  Under most circumstances, lexemes represent single words or numbers,
     *  although sequences of special symbols (such as mathematical notation
     *  or emoji) may be matched as well. This behavior may change in a future
     *  version.
     */
    def splitIntoLexemes(): Iterator[String] = lexemePattern.findAllIn(string)

    /** Splits a string into words. */
    def splitIntoWords(): Iterator[String] = splitIntoLexemes()
      .filter(isWord)
  }
}
