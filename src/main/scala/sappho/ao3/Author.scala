package sappho.ao3

import java.net.{URL, URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets

import scala.util.matching.Regex

sealed abstract class Author extends sappho.Author {
  def isPseud: Boolean
  def user: User
}
object Author {
  val urlPattern: Regex = "/users/([^/]*)/pseuds/([^/]*)$".r

  def apply(user: String, pseud: String): Author = {
    pseud match {
      case `user` => User(user)
      case _ => Pseud(user, pseud)
    }
  }

  def fromUrl(url: String): Author = {
    url match {
      case urlPattern(user, pseud) =>
        val decodedUser = URLDecoder.decode(user, StandardCharsets.UTF_8)
        val decodedPseud = URLDecoder.decode(pseud, StandardCharsets.UTF_8)
        Author(decodedUser, decodedPseud)
    }
  }
}

final case class User private(name: String) extends Author {
  override def url: URL = {
    val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8)
    new URL(s"https://archiveofourown.org/users/$encodedName")
  }

  override def isPseud: Boolean = false
  override def user: User = this

  override def toString: String = s"sappho.ao3.User(${'"'}${name}${'"'})"
}

final class Pseud private(val user: User, val name: String) extends Author {
  override def fullName: String = s"$name (${user.name})"

  override def url: URL = {
    val encodedUser = URLEncoder.encode(user.name, StandardCharsets.UTF_8)
    val encodedPseud = URLEncoder.encode(this.name, StandardCharsets.UTF_8)
    new URL(s"https://archiveofourown.org/users/$encodedUser/pseuds/$encodedPseud")
  }

  override def isPseud: Boolean = true


  override def equals(other: Any): Boolean = other match {
    case that: Pseud => this.user == that.user && this.name == that.name
    case _ => false
  }

  override def hashCode: Int = (user, name).hashCode
  override def toString: String = s"sappho.ao3.Pseud(${'"'}${user.name}${'"'}, ${'"'}$name${'"'})"
}
object Pseud {
  def apply(userName: String, pseudName: String): Pseud = new Pseud(User(userName), pseudName)
  def unapply(pseud: Pseud): (String, String) = (pseud.user.name, pseud.name)
}