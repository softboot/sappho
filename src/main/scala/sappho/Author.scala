package sappho

import java.net.URL

trait Author {
  def name: String
  def fullName: String = name
  def url: URL
}
