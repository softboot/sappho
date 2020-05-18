package sappho.ao3

import java.net.{URL, URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

import com.typesafe.scalalogging.LazyLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import sappho.util.Log._

import scala.util.matching.Regex

sealed abstract class Author extends sappho.Author {
  def isPseud: Boolean
  def user: User

  /** The place where the author lives, if specified. */
  def location: Option[String]

  /** The author's birthday, if specified. */
  def birthday: Option[LocalDate]
}
object Author {
  val urlPattern: Regex = "/users/([^/]*)/pseuds/([^/]*)$".r

  def apply(user: String, pseud: String)(implicit browser: Browser): Author = {
    pseud match {
      case `user` => User(user)
      case _ => Pseud(user, pseud)
    }
  }

  def fromUrl(url: String)(implicit browser: Browser): Author = {
    url match {
      case urlPattern(user, pseud) =>
        val decodedUser = URLDecoder.decode(user, StandardCharsets.UTF_8)
        val decodedPseud = URLDecoder.decode(pseud, StandardCharsets.UTF_8)
        Author(decodedUser, decodedPseud)
    }
  }
}

final class User private(val name: String, browser: Browser) extends Author with LazyLogging {
  override def joinedOn: LocalDate = {
    val dateString = (bioPage >> texts("dl.meta dd")).iterator.drop(1).next
    LocalDate.parse(dateString, ISO_LOCAL_DATE)
  }

  override def bio: Option[String] = (bioPage >?> texts("div.bio blockquote p"))
    .map(_.mkString("\n\n"))

  override def location: Option[String] = bioPage >?> text("dt.location + dd")

  override def birthday: Option[LocalDate] = (bioPage >?> text("dd.birthday"))
    .map(LocalDate.parse(_, ISO_LOCAL_DATE))

  override def url: URL = {
    val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8)
    new URL(s"https://archiveofourown.org/users/$encodedName")
  }

  override def isPseud: Boolean = false
  override def user: User = this

  override def equals(other: Any): Boolean = other match {
    case that: User => this.name == that.name
    case _ => false
  }

  override def hashCode: Int = name.hashCode

  override def toString: String = s"sappho.ao3.User(${'"'}${name}${'"'})"


  private lazy val bioPage: Document = browser.get(url.toString + "/profile", logger)
}
object User {
  def apply(userName: String)(implicit browser: Browser): User = new User(userName, browser)
  def unapply(user: User): Option[String] = Some(user.name)
}

final class Pseud private(val user: User, val name: String) extends Author {
  override def fullName: String = s"$name (${user.name})"
  override def joinedOn: LocalDate = user.joinedOn
  override def bio: Option[String] = user.bio
  override def location: Option[String] = user.location
  override def birthday: Option[LocalDate] = user.birthday

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
  def apply(userName: String, pseudName: String)(implicit browser: Browser): Pseud = new Pseud(User(userName), pseudName)
  def unapply(pseud: Pseud): Option[(String, String)] = Some((pseud.user.name, pseud.name))
}