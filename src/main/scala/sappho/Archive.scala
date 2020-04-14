package sappho

import java.net.URL

trait Archive {
  def name: String
  def abbreviation: String
  def homepage: URL
}
