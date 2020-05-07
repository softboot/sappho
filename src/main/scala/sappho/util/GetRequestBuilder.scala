package sappho.util

import java.net.{URL, URLEncoder}
import java.nio.charset.StandardCharsets.UTF_8

class GetRequestBuilder(url: URL) {
  import GetRequestBuilder._

  private val builder = new StringBuilder(url.toString)
  private var hasPreviousEntry: Boolean = false

  def add(name: String, value: String, encoder: String => String = urlEncode): GetRequestBuilder = {
    builder
      .append(if(hasPreviousEntry) '&' else '?')
      .append(encoder(name))
      .append("=")
      .append(encoder(value))
    hasPreviousEntry = true
    this
  }

  def addEmpty(name: String, encoder: String => String = urlEncode): GetRequestBuilder = {
    add(name, "", urlEncode)
  }

  def get: String = builder.toString


  override def hashCode(): Int = builder.hashCode

  override def equals(other: Any): Boolean = other match {
    case that: GetRequestBuilder => builder.equals(that.builder)
    case _ => false
  }

  override def toString: String = s"GetRequestBuilder($get)"
}

object GetRequestBuilder {
  def urlEncode(string: String): String = URLEncoder.encode(string, UTF_8)
}